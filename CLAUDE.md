# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Build & Test Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (APK output: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk)
./gradlew assembleRelease

# Run all unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.gyros.startchat.screens.startchat.StartChatViewModelTest"

# Run all instrumented (UI) tests — requires a connected device or emulator
./gradlew connectedAndroidTest

# Run a single instrumented test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.gyros.startchat.screens.startchat.StartChatScreenTest

# Lint
./gradlew lint
```

## Architecture

The app follows a layered architecture with Hilt for dependency injection.

```
UI (Compose screens)
    └── ViewModel (StateFlow + Channel events)
        └── Use Cases (domain layer)
            └── Repository (interface)
                └── Data sources (CountryCodesReader, StartChatSharedPreferences, ClipBoardManager)
```

### Entry points

`MainActivity` handles two modes based on the incoming Intent:

- **Launcher** (`ACTION_MAIN`) — renders `StartChatMainScreen`, which sets up the
  `ModalNavigationDrawer` + `MainNavHost` with two routes: `start_chat` and `about`.
- **Share target** (`ACTION_SEND`, `text/*`) — renders `StartChatScreenWithViewModel` directly as a
  transparent overlay dialog, passing the shared text as `actionText`. The activity finishes after
  launching WhatsApp.

### State pattern in StartChatViewModel

`StartChatState` is a data class that embeds callbacks (`onStartChat`, `onEditTextChange`,
`onCountryCodeSelected`) as lambdas. This means the UI never calls the ViewModel directly — it only
reads state. The ViewModel wires itself up by passing `::cleanText` and `::selectCountryCode` as the
initial callbacks.

- `onStartChat` is `null` when the phone number is invalid (disables the button) and set to
  `::startChat` when valid.
- Side effects (launching WhatsApp) are delivered via a one-shot `Channel<Events>` collected in
  `StartChatScreenWithViewModel`.

### Country codes

Country codes are loaded once at startup from `assets/country_codes.json` via `CountryCodesReader` (
Moshi). They are cached lazily in both `CountryCodeRepositoryImpl` and `StartChatViewModel`. The
selected country code dial code (e.g. `"+1"`) is persisted in `SharedPreferences` via
`StartChatSharedPreferences`.

`CountryCode.dialCode` always includes the `+` prefix (e.g. `"+57"`). Do not add an extra `+` when
building phone strings.

### Clipboard integration

On every `ON_RESUME` lifecycle event, `StartChatViewModel.onResume()` reads phone numbers from the
clipboard via `ClipBoardManager` and surfaces them in state as `numbersOnClipBoard`. Numbers
matching the current `phoneNumber` value are filtered out. The `ClipBoardManagerImpl` applies
`REGEX_VALID_PHONE_NUMBER` to filter non-phone clipboard content.

## Dependency Injection

All dependencies are scoped to `ViewModelComponent` in `StartChatModule`. There is a single Hilt
module — when adding new dependencies, add them there. `CountryCodesReader` receives the asset path
`"country_codes.json"` as a constructor argument injected manually in the module.

## Testing

- **Unit tests** (`app/src/test/`) use JUnit 4 + MockK. ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` and `Dispatchers.setMain`.
- **UI tests** (`app/src/androidTest/`) use Compose `createComposeRule()` and test `StartChatScreen`
  composable in isolation by passing `StartChatState` directly — no ViewModel or Hilt required.
- Shared mock helpers live in `app/src/test/java/com/gyros/startchat/StartChatMocks.kt` (unit tests
  only — not accessible from `androidTest`).
- Test assets (JSON fixtures) are in `app/src/test/assets/`.
