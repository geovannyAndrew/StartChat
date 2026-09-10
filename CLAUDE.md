# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Build & Test Commands

```bash
# Android debug APK
./gradlew assembleDebug

# Android release build
./gradlew assembleRelease
# APK output: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk

# iOS framework (required before Xcode)
./gradlew :app:compileKotlinIosSimulatorArm64
./gradlew :app:linkDebugFrameworkIosSimulatorArm64

# iOS simulator build (requires Kotlin framework built first; CODE_SIGNING_ALLOWED=NO
# because no development team is configured — simulator builds don't need signing.
# OS= disambiguates duplicated simulators; check with `xcrun simctl list devices available`)
xcodebuild -project iosApp/StartChat.xcodeproj -scheme StartChat \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15,OS=17.5' \
  CODE_SIGNING_ALLOWED=NO build

# iOS Share Extension build
xcodebuild -project iosApp/StartChat.xcodeproj -scheme ShareExtension \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15,OS=17.5' \
  CODE_SIGNING_ALLOWED=NO build

# Run all unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.gyros.startchat.screens.startchat.StartChatViewModelTest"

# Run all instrumented tests (requires a connected device or emulator)
./gradlew connectedAndroidTest

# Run iOS unit tests
./gradlew :app:iosSimulatorArm64Test

# Lint
./gradlew lint
```

## Architecture

Kotlin Multiplatform with three source sets: `commonMain` (shared), `androidMain` (Android),
`iosMain` (iOS). The app follows a layered architecture with [Koin](https://insert-koin.io/)
for dependency injection:

```
UI (Compose Multiplatform screens)
    └── ViewModel (StateFlow + Channel events)
        └── Use Cases (domain layer)
            └── Repository (interface)
                └── Data sources (Room DAO, CountryCodesReader, Settings, ClipBoardManager)
```

### Entry points

**Android** — `MainActivity` handles two modes based on the incoming Intent:

- **Launcher** (`ACTION_MAIN`) — renders `StartChatMainScreen`, which sets up the
  `ModalNavigationDrawer` + `MainNavHost` with three routes: `start_chat`, `history`, and `about`.
- **Share target** (`ACTION_SEND`, `text/*`) — renders `StartChatScreenForShare` directly as a
  transparent overlay dialog, pre-filling the shared text. The activity finishes after launching
  WhatsApp.

**iOS** — `App.kt` (`@Composable StartChatApp()`) and `MainViewController.kt` (
`MainUIViewController`).
The share extension handoff flows through `PendingSharedTextStore` (App Group UserDefaults).

### State pattern in StartChatViewModel

`StartChatState` is a data class that embeds callbacks (`onStartChat`, `onEditTextChange`,
`onCountryCodeSelected`, …) as lambdas. The UI never calls the ViewModel directly — it only
renders state and invokes the lambdas it was given. One-shot side effects (such as launching
WhatsApp) are delivered via a `Channel<Events>` collected by the screen.

- `onStartChat` is `null` when the phone number is invalid (disables the button) and set to
  `::startChat` when valid.

### Country codes

Country codes are parsed once at startup from `composeResources/country_codes.json` using
[kotlinx.serialization](https://kotlinlang.org/docs/serialization.html), cached lazily in both
`CountryCodeRepositoryImpl` and `StartChatViewModel`. The chosen dial code (e.g. `"+57"`) is
persisted via `multiplatform-settings`.

`CountryCode.dialCode` always includes the `+` prefix (e.g. `"+57"`). Do not add an extra `+`
when building phone strings.

### Clipboard integration

On every `ON_RESUME` lifecycle event, `StartChatViewModel.onResume()` reads the clipboard through
`ClipBoardManager`, filters content with `REGEX_VALID_PHONE_NUMBER`, and exposes valid matches as
`numbersOnClipBoard` (excluding numbers that match the number already typed).

### Chat history

Each chat that's started is persisted to a Room database (`StartChatDatabase` /
`ChatHistoryDao`) through `ChatHistoryRepository` and the `SaveChatHistoryEntryUseCase` /
`GetChatHistoryUseCase` use cases.

### Launching WhatsApp

`GetWhatsAppUriUseCase` builds a [`wa.me`](https://wa.me) deep link from the phone number
(stripping the `+`), e.g. `https://wa.me/15551234567`, which is opened via the platform
`UrlOpener` (`Intent` on Android, `UIApplication.openURL` on iOS).

## Project structure

```
app/src/commonMain/kotlin/com/gyros/startchat/
├── MainNavHost.kt                      # NavHost (multiplatform navigation-compose)
├── StartChatMainScreen.kt              # Main screen with drawer + nav host
├── screens/
│   ├── startchat/                      # StartChatScreen, StartChatScreenWithViewModel,
│   │                                    #   StartChatViewModel, StartChatState
│   ├── history/                        # ChatHistoryScreen, ChatHistoryScreenWithViewModel,
│   │                                    #   ChatHistoryViewModel
│   └── about/                          # AboutScreen, AboutScreenWithViewModel, AboutIcon
├── common/
│   ├── composables/                    # DropdownCountries
│   └── extensions/                    # String extensions
├── ui/theme/                           # Color, Theme, Type (Material3)
├── domain/                             # Use cases (one class per use case)
├── repositories/                       # Repository interfaces + implementations
├── data/
│   ├── models/                         # CountryCode, ChatHistoryEntry (Room @Entity)
│   ├── ChatHistoryDao.kt, StartChatDatabase.kt, Settings interfaces
│   └── (interfaces) ClipBoardManager, CountryCodesReader, UrlOpener, AppInfo
└── di/                                 # AppModule, ViewModelModule

app/src/androidMain/kotlin/com/gyros/startchat/
├── MainActivity.kt                     # Entry point, handles launcher & share-target intents
├── StartChatApplication.kt            # Koin application (startKoin)
├── data/                               # Android platform impls:
│                                      #   UrlOpenerImpl, AppInfoImpl, ClipBoardManagerImpl,
│                                      #   CountryCodesReaderImpl, PendingSharedTextStore,
│                                      #   SettingsImpl
├── di/DatabaseModule.kt               # Room builder + AndroidSQLiteDriver
├── screens/
│   ├── startchat/StartChatScreenBridge.kt  # Share-target overlay composable
│   └── about/AboutIcon.android.kt
└── common/extensions/ContextExt.kt

app/src/iosMain/kotlin/com/gyros/startchat/
├── App.kt                             # @Composable StartChatApp()
├── MainViewController.kt              # MainUIViewController (iOS entry)
├── data/                              # iOS platform impls:
│                                      #   UrlOpenerImpl, AppInfoImpl, ClipBoardManagerImpl,
│                                      #   CountryCodesReaderImpl, PendingSharedTextStore
├── di/IosModule.kt                    # Room builder + BundledSQLiteDriver + NSUserDefaults
└── screens/about/AboutIcon.ios.kt
```

## Dependency Injection

All dependencies are wired up in Koin modules. `commonMain` has `AppModule` (use cases, repos,
platform services) and `ViewModelModule`. `androidMain` adds `DatabaseModule` (Room builder +
AndroidSQLiteDriver). `iosMain` has `IosModule` (Room builder + BundledSQLiteDriver +
NSUserDefaultsSettings). `commonMain` uses `koinViewModel()` and `koinInject<T>()`.

## Testing

- **Unit tests** (`app/src/test/`) — JUnit 4 + MockK (JVM-only). ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` and `Dispatchers.setMain`. Shared mock
  helpers live in `StartChatMocks.kt`; JSON fixtures live in `app/src/test/assets/`.
- **Instrumented/UI tests** (`app/src/androidTest/`) — Compose `createComposeRule()`. Screens are
  tested in isolation by passing a `State` object directly (no ViewModel or Koin required), plus
  Room DAO tests run against an in-memory database.
- **iOS unit tests** (`app/src/iosTest/`) — `kotlin.test` framework with `FakeClock` and
  `FakePendingSharedTextStore` test doubles. Run via `./gradlew :app:iosSimulatorArm64Test`.

## Other instruction files

`README.md` and `AGENTS.md` describe the same architecture. `docs/kmp-migration-spec.md` records
the full Phase 1–4 KMP migration history. `docs/shared-navigation-spec.md` records the
shared-drawer/NavHost consolidation (Phase SN).
