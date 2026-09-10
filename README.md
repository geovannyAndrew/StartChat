# Start Chat

Start a WhatsApp chat with any phone number without saving it as a contact. Available on
**Android** and **iOS**. Enter a number manually, pick one up automatically from your clipboard,
or share text into the app from anywhere on your device — Start Chat extracts the number and
opens WhatsApp for you.

## Features

- **Start a chat instantly** — pick a country code, type a number, and jump straight into a
  WhatsApp conversation via [`wa.me`](https://wa.me) deep links (no contact required).
- **Clipboard detection** — on every app resume, Start Chat scans the clipboard for valid phone
  numbers and surfaces them as quick-pick suggestions.
- **Share-target integration** — share any text (e.g. from a messaging app, browser, or notes app)
  to Start Chat and it opens directly as a transparent overlay, ready to send to WhatsApp.
- **iOS Share Extension** — share text from any app via the iOS share sheet into Start Chat on iOS.
  Uses App Group handoff (`group.com.gyros.startchat`) with 10-minute TTL. SwiftUI-only extension
  (no Kotlin/Compose in appex). See `docs/ios-share-extension-spec.md`.
- **Country code picker** — searchable dropdown of country dial codes loaded from a bundled JSON
  asset; your last selection is remembered between sessions.
- **Chat history** — every chat you start is saved locally (Room database) with a timestamp, so you
  can revisit and re-open past conversations from the History screen.
- **About screen** — shows the app description and current version.

## Gif
<img width="340" alt="screen_recording" src="https://github.com/user-attachments/assets/cac3c357-5dab-483d-b794-1a66295f4d9c" />


## Screens & Navigation
### Android
| <img width="1080" height="2424" alt="Screenshot_1787025339" src="https://github.com/user-attachments/assets/b87c4210-71fe-4262-a1c8-8cc72044346a" /> | <img width="1080" height="2424" alt="Screenshot_20260819_203643" src="https://github.com/user-attachments/assets/a9978d07-aaf9-443a-919a-203188522172" /> | <img width="1080" height="2424" alt="Screenshot_1787025377" src="https://github.com/user-attachments/assets/59f10aca-88e3-4395-90c9-2dea55cc9a6b" /> |
|:---:|:---:|:---:|

### iOS
| <img width="1206" height="2622" alt="Simulator Screenshot - iPhone 17 - 2026-09-09 at 18 44 54" src="https://github.com/user-attachments/assets/621a049d-3f7d-44e0-82e8-9088712de1b8" /> | <img width="1206" height="2622" alt="Simulator Screenshot - iPhone 17 - 2026-09-09 at 18 45 15" src="https://github.com/user-attachments/assets/0fa09e7b-c278-4d3f-9748-8773884f0086" /> | <img width="1206" height="2622" alt="Simulator Screenshot - iPhone 17 - 2026-09-09 at 18 45 19" src="https://github.com/user-attachments/assets/48c61467-e4e4-4a3a-985a-5e133e15dcc2" /> | <img width="1206" height="2622" alt="Simulator Screenshot - iPhone 17 - 2026-09-09 at 18 45 27" src="https://github.com/user-attachments/assets/a17a28ec-808d-465b-bfdf-5edc2fa89239" /> |
| --- | --- | --- | --- |

The app uses a `ModalNavigationDrawer` with a `NavHost` exposing three destinations:

| Route         | Screen               | Description                                              |
|---------------|----------------------|----------------------------------------------------------|
| `start_chat`  | `StartChatScreen`    | Main entry point — enter/select a number and start a chat |
| `history`     | `ChatHistoryScreen`  | List of previously started chats with timestamps         |
| `about`       | `AboutScreen`        | App description and version info                         |

Entry points differ per platform:

**Android** — `MainActivity` handles two modes based on the incoming Intent:

- **Launcher** (`ACTION_MAIN`) — renders `StartChatMainScreen` (drawer + nav host with routes
  `start_chat`/`history`/`about`).
- **Share target** (`ACTION_SEND`, `text/*`) — renders `StartChatScreenForShare` as a transparent
  overlay dialog, pre-filling the shared text. The activity finishes after WhatsApp is launched.

**iOS** — `MainViewController` + `App` composable. The share extension writes to App Group
UserDefaults (`PendingSharedTextStore`); the main app reads pending text on launch.

## Architecture

The app follows a layered architecture with [Koin](https://insert-koin.io/) for dependency
injection:

```
UI (Compose Multiplatform screens)
    └── ViewModel (StateFlow + Channel events)
        └── Use Cases (domain layer)
            └── Repository (interface)
                └── Data sources (Room DAO, CountryCodesReader, Settings, ClipBoardManager)
```

### State pattern

`StartChatState` is a data class that embeds its own callbacks (`onStartChat`,
`onEditTextChange`, `onCountryCodeSelected`, …) as lambdas. The UI never calls the ViewModel
directly — it only renders state and invokes the lambdas it was given. One-shot side effects (such
as launching WhatsApp) are delivered through a `Channel<Events>` collected by the screen.

### Country codes

Country codes are parsed once at startup from `composeResources/country_codes.json` using
[kotlinx.serialization](https://kotlinlang.org/docs/serialization.html), cached lazily in both
`CountryCodeRepositoryImpl` and `StartChatViewModel`. The chosen dial code (e.g. `"+57"`) is
persisted via `multiplatform-settings`. `CountryCode.dialCode` always includes the `+` prefix — it
is not added again when building the WhatsApp URI.

### Clipboard integration

On every `ON_RESUME` lifecycle event, `StartChatViewModel.onResume()` reads the clipboard through
`ClipBoardManager`, filters content with `REGEX_VALID_PHONE_NUMBER`, and exposes valid matches as
`numbersOnClipBoard` (excluding numbers that match the number already entered).

### Chat history

Each chat that's started is persisted to a Room database (`StartChatDatabase` /
`ChatHistoryDao`) through `ChatHistoryRepository` and the `SaveChatHistoryEntryUseCase` /
`GetChatHistoryUseCase` use cases, then displayed with a formatted timestamp on
`ChatHistoryScreen`.

### Launching WhatsApp

`GetWhatsAppUriUseCase` builds a [`wa.me`](https://wa.me) deep link from the phone number
(stripping the `+`), e.g. `https://wa.me/15551234567`, which is opened via the platform
`UrlOpener` (Android `Intent`, iOS `UIApplication.openURL`).

## Project structure

Kotlin Multiplatform with three source sets:

```
app/src/commonMain/kotlin/com/gyros/startchat/
├── MainNavHost.kt                      # NavHost (multiplatform navigation-compose)
├── StartChatMainScreen.kt             # Main screen with drawer + nav host
├── screens/
│   ├── startchat/                      # StartChatScreen, StartChatScreenWithViewModel,
│   │                                    #   StartChatViewModel, StartChatState
│   ├── history/                        # ChatHistoryScreen, ChatHistoryScreenWithViewModel,
│   │                                    #   ChatHistoryViewModel
│   └── about/                          # AboutScreen, AboutScreenWithViewModel, AboutIcon
├── common/
│   ├── composables/                    # Shared composables (e.g. DropdownCountries)
│   └── extensions/                     # String extensions
├── ui/theme/                           # Compose Material3 theme (Color, Theme, Type)
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

## Tech stack

- **Language**: Kotlin 2.2.0
- **UI**: Compose Multiplatform 1.7.3 (Material 3)
- **DI**: Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`)
- **Async**: Kotlin Coroutines + `StateFlow` / `Channel`
- **Persistence**: Room 2.7.0 (chat history), `multiplatform-settings` (last-used country code)
- **Navigation**: navigation-compose (multiplatform)
- **JSON parsing**: kotlinx.serialization (country codes)
- **Min SDK**: 24 · **Target/Compile SDK**: 36 · **iOS**: 15+

## Getting started

### Prerequisites

- JDK 17
- Android SDK (for Android builds)
- Xcode 15+ (for iOS builds)
- An Android device or emulator running API 24+ (for Android)

### Build & run

```bash
# Android debug APK
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug

# iOS simulator (requires Kotlin framework built first; CODE_SIGNING_ALLOWED=NO because
# no development team is configured — simulator builds don't need signing. OS= disambiguates
# duplicated simulators; check with `xcrun simctl list devices available`)
./gradlew :app:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/StartChat.xcodeproj -scheme StartChat \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15,OS=17.5' \
  CODE_SIGNING_ALLOWED=NO build

# iOS Share Extension (requires Kotlin framework built first)
xcodebuild -project iosApp/StartChat.xcodeproj -scheme ShareExtension \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15,OS=17.5' \
  CODE_SIGNING_ALLOWED=NO build
```

### Release build (Android)

```bash
./gradlew assembleRelease
# APK output: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk
```

## Testing

- **Unit tests** (`app/src/test/`) — JUnit 4 + MockK (JVM-only). ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` and `Dispatchers.setMain`. Shared mock
  helpers live in `StartChatMocks.kt`; JSON fixtures live in `app/src/test/assets/`.
- **Instrumented/UI tests** (`app/src/androidTest/`) — Compose `createComposeRule()`. Screens are
  tested in isolation by passing a `State` object directly (no ViewModel or Koin required), plus
  Room DAO tests run against an in-memory database.
- **iOS unit tests** (`app/src/iosTest/`) — `kotlin.test` framework with `FakeClock` and
  `FakePendingSharedTextStore` test doubles. Run via `./gradlew :app:iosSimulatorArm64Test`.

```bash
# Run all unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.gyros.startchat.screens.startchat.StartChatViewModelTest"

# Run all instrumented tests (requires a connected device or emulator)
./gradlew connectedAndroidTest

# Run a single instrumented test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.gyros.startchat.screens.startchat.StartChatScreenTest

# Lint
./gradlew lint

# iOS unit tests
./gradlew :app:iosSimulatorArm64Test
```

## Dependency injection

All dependencies are wired up in Koin modules: `AppModule` (use cases, repos, platform services),
`DatabaseModule` (Android: Room builder + AndroidSQLiteDriver), and `IosModule` (iOS: Room builder +
BundledSQLiteDriver + NSUserDefaultsSettings). When adding new dependencies, add them to the
appropriate module. `commonMain` uses `koinViewModel()` and `koinInject<T>()`.

## License

No license file is currently included in this repository.
