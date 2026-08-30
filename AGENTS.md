# AGENTS.md

Single-module Android app (`:app`): Kotlin 2.0, Jetpack Compose + Material 3, Hilt, Room, Moshi.
Starts WhatsApp chats from typed/clipboard/shared phone numbers via `wa.me` deep links.

## Commands

```bash
./gradlew assembleDebug
./gradlew assembleRelease          # APK: app/build/outputs/apk/release/start_chat_YYYYMMDD.apk (date-stamped name)
./gradlew test                     # all unit tests
./gradlew test --tests "com.gyros.startchat.screens.startchat.StartChatViewModelTest"  # single class
./gradlew connectedAndroidTest     # instrumented tests — requires device/emulator
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.gyros.startchat.screens.startchat.StartChatScreenTest
./gradlew lint
```

No CI, formatter, or typecheck config exists — `lint` is the only static check.

## Architecture

Layered: Compose screen → ViewModel (StateFlow + one-shot `Channel<Events>` for side effects) →
use case (`domain/`, one class per use case) → repository interface (`repositories/`) →
data sources (`data/`: Room DAO, `CountryCodesReader`, `StartChatSharedPreferences`,
`ClipBoardManager`).

- `MainActivity` has two modes: launcher (`ACTION_MAIN` → `StartChatMainScreen`, drawer +
  `MainNavHost` with routes `start_chat`, `history`, `about`) and share target
  (`ACTION_SEND` `text/*` → `StartChatScreenWithViewModel` as transparent overlay; activity
  finishes after launching WhatsApp).
- `StartChatState` embeds callbacks (`onStartChat`, `onEditTextChange`, …) as lambdas — the UI
  never calls the ViewModel directly. `onStartChat` is `null` while the number is invalid
  (this is how the button gets disabled).
- DI: `StartChatModule` (`@InstallIn(ViewModelComponent)`) and `DatabaseModule` (Room). Add new
  bindings there. `CountryCodesReader` gets the asset path `"country_codes.json"` as a
  manually-provided constructor argument.

## Gotchas

- `CountryCode.dialCode` already includes the `+` prefix (e.g. `"+57"`); `GetWhatsAppUriUseCase`
  strips it when building `https://wa.me/<number>`. Don't add/remove `+` elsewhere.
- Clipboard is scanned on every `ON_RESUME` (`StartChatViewModel.onResume()`) via
  `ClipBoardManager`, filtered with `REGEX_VALID_PHONE_NUMBER`; matches become
  `numbersOnClipBoard` (excluding the number already typed).
- Compiler wiring: KSP for Hilt and Room compilers (`ksp(...)` in `app/build.gradle.kts`).

## Testing

- Unit tests (`app/src/test/`): JUnit 4 + MockK; ViewModel tests use
  `kotlinx-coroutines-test` with `StandardTestDispatcher` + `Dispatchers.setMain`.
- Instrumented tests (`app/src/androidTest/`): Compose `createComposeRule()` — screens are tested
  by passing a `State` object directly, no ViewModel/Hilt. Room DAO tests use in-memory DB.
- Shared mock helpers in `app/src/test/java/com/gyros/startchat/StartChatMocks.kt` are unit-test
  only, not visible to `androidTest`. JSON fixtures live in `app/src/test/assets/`.

## Other instruction files

`CLAUDE.md` and `README.md` describe the same architecture in more detail, but `CLAUDE.md`
predates the chat-history feature (it omits the `history` route, Room, and `DatabaseModule`) —
trust this file and `README.md` over it.
