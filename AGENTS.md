# AGENTS.md

Kotlin Multiplatform app (`:app`): Kotlin 2.2.0, Compose Multiplatform 1.7.3, Koin DI, Room 2.7.0,
kotlinx.serialization, multiplatform-settings. Starts WhatsApp chats from typed/clipboard/shared
phone numbers via `wa.me` deep links. iOS target via `iosSimulatorArm64`.

## Commands

```bash
./gradlew assembleDebug           # Android debug APK
./gradlew assembleRelease         # APK: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk (date-stamped)
./gradlew test                   # all unit tests
./gradlew test --tests "com.gyros.startchat.screens.startchat.StartChatViewModelTest"  # single class
./gradlew connectedAndroidTest    # instrumented tests — requires device/emulator
./gradlew lint                   # Android lint

# iOS framework (required before Xcode)
./gradlew :app:compileKotlinIosSimulatorArm64
./gradlew :app:linkDebugFrameworkIosSimulatorArm64

# iOS simulator build (requires Kotlin framework built first)
xcodebuild -project iosApp/StartChat.xcodeproj -scheme StartChat \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15' build
```

No CI, formatter, or typecheck config exists — `lint` is the only static check.

## Architecture

Kotlin Multiplatform with three source sets:

- `commonMain/`: shared UI (Compose Multiplatform), ViewModels, domain use cases, repositories,
  repository implementations, Room DAO/DB, Koin DI modules (`AppModule`, `ViewModelModule`).
- `androidMain/`: Android platform impls (`UrlOpenerImpl`, `AppInfoImpl`, `ClipBoardManagerImpl`,
  `CountryCodesReaderImpl`), `DatabaseModule` (Room builder + Android SQLite driver), Android-only
  `StartChatApplication`, `MainActivity` (launcher + `ACTION_SEND` share target).
- `iosMain/`: iOS platform impls (`UrlOpenerImpl`, `AppInfoImpl`, `ClipBoardManagerImpl`,
  `CountryCodesReaderImpl`), `IosModule` (Room builder + `BundledSQLiteDriver`,
  `NSUserDefaultsSettings`).

DI wiring per platform via Koin modules. `commonMain` uses `koinViewModel()` and `koinInject<T>()`.
Platform services (`UrlOpener`, `AppInfo`, `ClipBoardManager`, `CountryCodesReader`, `Settings`)
are registered per-platform in `DatabaseModule` (android) / `IosModule` (ios).

`MainActivity` has two modes: launcher (`ACTION_MAIN` → `StartChatMainScreen`, drawer +
`MainNavHost` with routes `start_chat`/`history`/`about`) and share target
(`ACTION_SEND` `text/*` → `StartChatScreenForShare` as transparent overlay; activity finishes after
launching WhatsApp).

`StartChatState` embeds callbacks (`onStartChat`, `onEditTextChange`, …) as lambdas — the UI never
calls the ViewModel directly. `onStartChat` is `null` while the number is invalid (this is how the
button gets disabled).

## Gotchas

- `CountryCode.dialCode` already includes the `+` prefix (e.g. `"+57"`); `GetWhatsAppUriUseCase`
  strips it when building `https://wa.me/<number>`. Don't add/remove `+` elsewhere.
- Clipboard is scanned on every `ON_RESUME` (`StartChatViewModel.onResume()`) via
  `ClipBoardManager`, filtered with `REGEX_VALID_PHONE_NUMBER`; matches become
  `numbersOnClipBoard` (excluding the number already typed).
- Room schema lives in `app/schemas/`; KSP generates it. Do not modify manually.
- iOS framework must be rebuilt via Gradle before every Xcode build
  (`linkDebugFrameworkIosSimulatorArm64`); the Kotlin framework path is
  `app/build/bin/iosSimulatorArm64/debugFramework/StartChat.framework`.
- The iOS simulator lacks WhatsApp; `wa.me` URL opens the App Store fallback.

## Testing

- Unit tests (`app/src/test/`): JUnit 4 + MockK; ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` + `Dispatchers.setMain`.
- Instrumented tests (`app/src/androidTest/`): Compose `createComposeRule()` — screens are tested
  by passing a `State` object directly, no ViewModel/DI. Room DAO tests use in-memory DB.
- Shared mock helpers in `app/src/test/java/com/gyros/startchat/StartChatMocks.kt` are unit-test
  only, not visible to `androidTest`. JSON fixtures live in `app/src/test/assets/`.

## Other instruction files

`CLAUDE.md` and `README.md` describe the same architecture in more detail, but `CLAUDE.md`
predates the KMP migration — trust this file and `README.md` over it.
`docs/kmp-migration-spec.md` records the full Phase 1–3 KMP migration history.
`docs/shared-navigation-spec.md` records the shared-drawer/NavHost consolidation (Phase SN).
