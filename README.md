# Start Chat

Start a WhatsApp chat with any phone number without saving it as a contact. Enter a number
manually, pick one up automatically from your clipboard, or share text into the app from anywhere
on your device — Start Chat extracts the number and opens WhatsApp for you.

## Features

- **Start a chat instantly** — pick a country code, type a number, and jump straight into a
  WhatsApp conversation via [`wa.me`](https://wa.me) deep links (no contact required).
- **Clipboard detection** — on every app resume, Start Chat scans the clipboard for valid phone
  numbers and surfaces them as quick-pick suggestions.
- **Share-target integration** — share any text (e.g. from a messaging app, browser, or notes app)
  to Start Chat and it opens directly as a transparent overlay, ready to send to WhatsApp.
- **Country code picker** — searchable dropdown of country dial codes loaded from a bundled JSON
  asset; your last selection is remembered between sessions.
- **Chat history** — every chat you start is saved locally (Room database) with a timestamp, so you
  can revisit and re-open past conversations from the History screen.
- **About screen** — shows the app description and current version.

## Screens & Navigation

The app uses a `ModalNavigationDrawer` with a `NavHost` exposing three destinations:

| Route         | Screen               | Description                                              |
|---------------|----------------------|----------------------------------------------------------|
| `start_chat`  | `StartChatScreen`    | Main entry point — enter/select a number and start a chat |
| `history`     | `ChatHistoryScreen`  | List of previously started chats with timestamps         |
| `about`       | `AboutScreen`        | App description and version info                         |

`MainActivity` behaves differently depending on how it's launched:

- **Launcher** (`ACTION_MAIN`) — renders `StartChatMainScreen` (drawer + nav host).
- **Share target** (`ACTION_SEND`, `text/*`) — renders `StartChatScreenWithViewModel` directly as a
  transparent overlay dialog, pre-filling the shared text. The activity finishes after WhatsApp is
  launched.

## Architecture

The app follows a layered architecture with [Hilt](https://dagger.dev/hilt/) for dependency
injection:

```
UI (Compose screens)
    └── ViewModel (StateFlow + Channel events)
        └── Use Cases (domain layer)
            └── Repository (interface)
                └── Data sources (Room DAO, CountryCodesReader, SharedPreferences, ClipBoardManager)
```

### State pattern

`StartChatState` is a data class that embeds its own callbacks (`onStartChat`,
`onEditTextChange`, `onCountryCodeSelected`, …) as lambdas. The UI never calls the ViewModel
directly — it only renders state and invokes the lambdas it was given. One-shot side effects (such
as launching WhatsApp) are delivered through a `Channel<Events>` collected by the screen.

### Country codes

Country codes are parsed once at startup from `assets/country_codes.json` using
[Moshi](https://github.com/square/moshi), cached lazily in both `CountryCodeRepositoryImpl` and
`StartChatViewModel`. The chosen dial code (e.g. `"+57"`) is persisted via
`StartChatSharedPreferences`. `CountryCode.dialCode` always includes the `+` prefix — it is not
added again when building the WhatsApp URI.

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
(stripping the `+`), e.g. `https://wa.me/15551234567`, which is opened via an implicit `Intent`.

## Project structure

```
app/src/main/java/com/gyros/startchat/
├── MainActivity.kt                 # Entry point, handles launcher & share-target intents
├── MainNavHost.kt                  # NavHost wiring start_chat / history / about routes
├── StartChatMainScreen.kt          # Drawer + nav host scaffold
├── StartChatApplication.kt         # Hilt application class
├── di/                             # Hilt modules (StartChatModule, DatabaseModule)
├── data/                           # Data sources: Room DB/DAO, SharedPreferences,
│                                   #   ClipBoardManager, CountryCodesReader, models
├── repositories/                   # Repository interfaces + implementations
├── domain/                         # Use cases (one class per use case)
├── screens/
│   ├── startchat/                  # StartChatScreen + StartChatViewModel + state
│   ├── history/                    # ChatHistoryScreen + ChatHistoryViewModel
│   └── about/                      # AboutScreen
├── common/
│   ├── composables/                # Shared composables (e.g. DropdownCountries)
│   └── extensions/                 # Kotlin extensions (String, Context)
└── ui/theme/                       # Compose Material3 theme (Color, Theme, Type)
```

## Tech stack

- **Language**: Kotlin (2.0)
- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt (`ViewModelComponent` scope)
- **Async**: Kotlin Coroutines + `StateFlow` / `Channel`
- **Persistence**: Room (chat history), `SharedPreferences` (last-used country code)
- **Navigation**: Jetpack Navigation Compose
- **JSON parsing**: Moshi (country codes asset)
- **Min SDK**: 24 · **Target/Compile SDK**: 36

## Getting started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 11
- An Android device or emulator running API 24+

### Build & run

```bash
# Clone and open in Android Studio, or build from the command line:
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug
```

### Release build

```bash
./gradlew assembleRelease
# APK output: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk
```

## Testing

- **Unit tests** (`app/src/test/`) — JUnit 4 + MockK. ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` and `Dispatchers.setMain`. Shared mock
  helpers live in `StartChatMocks.kt`; JSON fixtures live in `app/src/test/assets/`.
- **Instrumented/UI tests** (`app/src/androidTest/`) — Compose `createComposeRule()`. Screens are
  tested in isolation by passing a `State` object directly (no ViewModel or Hilt required), plus
  Room DAO tests run against an in-memory database.

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
```

## Dependency injection

All dependencies are scoped to `ViewModelComponent` and wired up in a single Hilt module,
`StartChatModule` (plus `DatabaseModule` for Room). When adding new dependencies, add them there.
`CountryCodesReader` receives the asset path `"country_codes.json"` as a manually-injected
constructor argument.

## License

No license file is currently included in this repository.
