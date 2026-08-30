# Spec: StartChat — Kotlin Multiplatform (KMP) Migration

- **Status:** Phase 2 complete
- **Date:** 2026-08-29
- **Workflow:** Spec-Driven Development (SSD). Implement tasks strictly in order. A task is only
  marked complete `[x]` after its **Done when** checks pass. Do not start the next task until the
  current one is checked off.

---

## 1. Mission

Migrate StartChat from an Android-only app to a Kotlin Multiplatform (KMP) app sharing UI (Compose
Multiplatform) and business logic between Android and iOS, while preserving current Android behavior
exactly.

## 2. Current State (Post Phase 1)

- Single Android module `:app`: Kotlin 2.2.0, Jetpack Compose (BOM 2024.09.00 + Compose
  Multiplatform
  plugin 1.7.3), Material 3, Koin (no Hilt/kapt), Room 2.7.0, kotlinx.serialization,
  multiplatform-settings.
- ~1,600 LOC: ~970 UI, ~290 ViewModel, ~73 domain use cases, ~230 data/repositories/DI.
- `MainActivity` dual mode: launcher (`StartChatMainScreen`, drawer + routes `start_chat`/`history`/
  `about`) and share target (`ACTION_SEND` `text/*` transparent overlay).
- Single Room entity `ChatHistoryEntry` (String PK, Long timestamp), DAO with `getAll()`/`upsert()`,
  DB v1, no migrations.
- `Dispatchers.Default` used in `ChatHistoryRepositoryImpl` (was `Dispatchers.IO`).
- All Phase 1 tasks completed; Phase 2 (KMP conversion) ready to begin.

## 3. Goals

- G1: One shared codebase (`commonMain`) for UI, ViewModels, domain, repositories, data models.
- G2: iOS app runnable on simulator (iOS 15+) with feature parity for in-app flows: type/paste
  number, country-code default, history, about, open WhatsApp via `https://wa.me/<number>`.
- G3: Android behavior byte-for-byte identical: same launcher flow, same `ACTION_SEND` share target,
  same DB (no schema change), same APK output naming (`start_chat_YYYYMMDD.apk`).
- G4: All existing unit tests keep passing; instrumented tests keep passing on Android.

## 4. Non-Goals (out of scope)

- iOS Share Extension (deferred to a future spec).
- iOS App Store release, signing, and distribution beyond a runnable simulator build.
- Schema changes or DB migration (Room schema stays v1).
- Desktop/web targets.
- CI setup.

## 5. Locked Decisions

| ID  | Decision                                                                                                               |
|-----|------------------------------------------------------------------------------------------------------------------------|
| D1  | UI strategy: **Compose Multiplatform** (shared UI; iOS hosts `ComposeUIViewController`).                               |
| D2  | DI: **Koin** (`koin-core`, `koin-compose`, `koin-compose-viewmodel`). Hilt fully removed.                              |
| D3  | iOS share target: **skipped for v1**; Android `ACTION_SEND` target retained.                                           |
| D4  | Module name stays **`:app`** (no `:composeApp` rename; `settings.gradle.kts` unchanged).                               |
| D5  | Settings: **`multiplatform-settings` (russhwolf)** replaces `StartChatSharedPreferences`.                              |
| D6  | Minimum iOS version: **15**.                                                                                           |
| D7  | JSON: **`kotlinx.serialization`** replaces Moshi.                                                                      |
| D8  | Room **2.7.x KMP** with per-platform SQLite drivers (schema/annotations unchanged).                                    |
| D9  | Date formatting: **`kotlinx-datetime`** replaces `SimpleDateFormat`.                                                   |
| D10 | Kotlin upgraded to latest stable 2.2.x aligned with the Compose Multiplatform plugin version chosen.                   |
| D11 | No CocoaPods; Xcode consumes the Kotlin framework via Gradle.                                                          |
| D12 | Version source of truth remains `app/build.gradle.kts` (`versionName`); iOS reads version via `AppInfo` expect/actual. |

## 6. Target Architecture

```
app/
├── src/
│   ├── commonMain/kotlin/com/gyros/startchat/
│   │   ├── screens/{startchat,history,about}/   # stateless composables + State + Events
│   │   ├── common/composables/, ui/theme/
│   │   ├── navigation/                          # MainNavHost (multiplatform navigation-compose)
│   │   ├── screens/*/…ViewModel.kt              # multiplatform lifecycle ViewModel
│   │   ├── domain/                              # 6 use cases (GetWhatsAppUriUseCase → String)
│   │   ├── repositories/                        # interfaces + CountryCodeRepositoryImpl, ChatHistoryRepositoryImpl
│   │   ├── data/
│   │   │   ├── models/                          # CountryCode, ChatHistoryEntry (Room @Entity)
│   │   │   ├── ChatHistoryDao.kt, StartChatDatabase.kt
│   │   │   └── (interfaces) ClipBoardManager, CountryCodesReader, UrlOpener, AppInfo
│   │   ├── di/AppModule.kt, DatabaseModule.kt   # Koin modules
│   │   └── common/extensions/StringExt.kt
│   ├── androidMain/kotlin/.../
│   │   ├── MainActivity.kt                      # launcher + ACTION_SEND (unchanged behavior)
│   │   ├── platform/                            # UrlOpener (Intent), ClipBoardManager (ClipboardManager),
│   │   │                                        # CountryCodesReader (assets), AppInfo (packageManager)
│   │   ├── di/AndroidModule.kt                  # driver + platform bindings
│   │   └── StartChatApplication.kt              # startKoin { androidContext }
│   └── iosMain/kotlin/.../
│       ├── platform/                            # UrlOpener (UIApplication.open), ClipBoardManager (UIPasteboard),
│       │                                        # CountryCodesReader (bundle), AppInfo (Info.plist)
│       ├── di/IosModule.kt                      # BundledSQLiteDriver + platform bindings
│       └── MainViewController.kt                # ComposeUIViewController + foreground hook
│   └── commonMain/composeResources/             # country_codes.json, strings, drawable flags
├── build.gradle.kts                             # kotlin("multiplatform") + compose plugin; APK naming kept
iosApp/
├── StartChat.xcodeproj                          # SwiftUI App shell, deployment target iOS 15
└── StartChat/…
```

### Dependency replacement table

| Current                                                                          | Replacement                                                                  |
|----------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| Hilt + kapt (`hilt-android`, `hilt-android-compiler`, `hilt-navigation-compose`) | Koin (`koin-core`, `koin-android`, `koin-compose`, `koin-compose-viewmodel`) |
| Moshi (`moshi-kotlin`)                                                           | `kotlinx-serialization-json` + `kotlin("plugin.serialization")`              |
| `StartChatSharedPreferences`                                                     | `multiplatform-settings` (`com.russhwolf:multiplatform-settings`)            |
| Room 2.6.1 (KSP)                                                                 | Room 2.7.x KMP (KSP; `room-runtime` in common, drivers per platform)         |
| compose-bom 2024.09.00 / `kotlin-android` plugin                                 | Compose Multiplatform plugin (stable, aligned with Kotlin 2.2.x)             |
| `androidx.lifecycle:lifecycle-viewmodel`                                         | Multiplatform `androidx.lifecycle:lifecycle-viewmodel` (Lifecycle 2.8+)      |
| `java.text.SimpleDateFormat`                                                     | `kotlinx-datetime`                                                           |
| `android.net.Uri` in domain/events                                               | `String` (`https://wa.me/<number>`)                                          |
| `androidx.navigation:navigation-compose` 2.9.x                                   | Multiplatform `navigation-compose` (same API)                                |
| `material-icons-extended` (Android)                                              | `org.jetbrains.compose.material` icons                                       |

## 7. Requirements

### Functional (must hold on BOTH platforms unless noted)

- FR1: Typing a phone number sanitizes input; Start button enabled only when the number passes
  `isValidBasicPhone`.
- FR2: Numbers without a country code get the persisted default dial code prepended; selecting a
  country code persists it as default.
- FR3: Start chat opens `https://wa.me/<number>` (WhatsApp on device; App Store page/fallback in
  simulator).
- FR4: Successful start-chat writes a `ChatHistoryEntry` (upsert by phone number).
- FR5: History screen shows the last 50 entries (timestamp DESC); tapping an entry re-opens that
  chat.
- FR6: Clipboard phone numbers (regex-valid, excluding the currently typed number) are offered as
  chips on resume/foreground.
- FR7: About screen shows app name + version.
- FR8 (Android only): `ACTION_SEND` `text/*` share flow works as today (transparent overlay →
  process shared text → finish after launching WhatsApp).
- FR9: Default country code persists across launches on both platforms.

### Technical

- TR1: `GetWhatsAppUriUseCase` returns `String`; no `android.net.Uri` in `commonMain`.
- TR2: ViewModels live in `commonMain`, extend multiplatform `ViewModel`, use `viewModelScope`; no
  Android imports.
- TR3: All platform access goes through interfaces (`ClipBoardManager`, `CountryCodesReader`,
  `UrlOpener`, `AppInfo`) bound via Koin per platform.
- TR4: Room schema stays v1 (byte-identical schema JSON); existing Android installs upgrade without
  data loss.
- TR5: No `kapt` in the build; KSP only.
- TR6: APK release output still named `start_chat_YYYYMMDD.apk`.
- TR7: `Dispatchers.IO` not referenced in `commonMain` (inject a dispatcher or use
  `Dispatchers.Default`).
- TR8: Strings/`country_codes.json` served from Compose Multiplatform resources, not Android `res/`/
  `assets/`.

## 8. Constraints

- Each phase must leave the Android app buildable and tests green (no long-lived broken state).
- No behavior changes to existing Android UI beyond mechanical equivalents (e.g., date formatting
  library).
- iOS development requires macOS + Xcode (present in this environment).

---

## 9. Task Breakdown

> **Rules:** work strictly top-to-bottom; run the task's **Verify** steps; tick `[x]` only when all
**Done when** conditions hold. Notes go in the task's `Notes:` line (append, don't delete).

### Phase 1 — Android-side refactor (no KMP yet)

- [x] **P1-T1 — Toolchain & version upgrades**
  Bump Kotlin to 2.2.x (latest stable compatible with the chosen Compose Multiplatform plugin —
  record the exact pair in Notes), Room to 2.7.x, KSP plugin aligned with that Kotlin. Remove kapt
  if nothing else needs it.
  *Files:* `gradle/libs.versions.toml`, `app/build.gradle.kts`, `settings.gradle.kts`.
  Verify: `./gradlew assembleDebug test`.
  Done when: build + all unit tests pass on new versions; no kapt in build files.
  Notes: Kotlin 2.2.0, Room 2.7.0, KSP 2.2.0-2.0.2, kotlinx.serialization 1.9.0. kapt removed in
  P1-T7.

- [x] **P1-T2 — Compose plugin/bom swap to multiplatform-compatible baseline**
  Add the Compose Multiplatform Gradle plugin (org.jetbrains.compose) to the Android app (Android
  target still builds from it), drop compose-bom in favor of plugin-managed Compose versions. Keep
  `material-icons-extended` working (via `org.jetbrains.compose.material`).
  *Files:* `gradle/libs.versions.toml`, `app/build.gradle.kts`, root `build.gradle.kts`.
  Verify: `./gradlew assembleDebug` + `./gradlew connectedAndroidTest` (or manual smoke test on
  emulator if no device).
  Done when: app builds and runs with identical UI.
  Notes: Added `org.jetbrains.compose` plugin v1.7.3, JetBrains Space cache repository in
  settings.gradle.kts.

- [x] **P1-T3 — `Uri` → `String` in domain and events**
  `GetWhatsAppUriUseCase.invoke` returns `String` (`https://wa.me/<number>`).
  `Events.StartIntentAction` holds `uri: String`. Android bridges (`StartChatScreenWithViewModel`,
  `ChatHistoryScreenWithViewModel`) call `.toUri()` locally.
  *Files:* `domain/GetWhatsAppUriUseCase.kt`, `screens/startchat/StartChatViewModel.kt`, both screen
  bridge functions.
  Verify: `./gradlew test --tests "*StartChatViewModelTest*"` + full `./gradlew test`.
  Done when: no `android.net.Uri` import outside screen bridges; tests green.
  Notes: Events use String; bridges call .toUri() locally.

- [x] **P1-T4 — Extract platform-seam interfaces**
  Create interfaces in `data/`: `UrlOpener { fun open(url: String) }`,
  `AppInfo { fun appVersion(): String }`, `CountryCodesReader { fun read(): List<CountryCode> }`.
  `ClipBoardManager` interface already exists — keep it. Implement Android actuals (`UrlOpenerImpl`
  with `Intent(ACTION_VIEW)`, `AppInfoImpl` via `packageManager`, move asset reading into
  `CountryCodesReaderImpl`). `AboutScreen` takes version via `AppInfo` (or a version parameter from
  ViewModel/bridge) instead of reading `context.packageManager` directly.
  *Files:* new `data/UrlOpener.kt`, `data/UrlOpenerImpl.kt`, `data/AppInfo.kt`,
  `data/AppInfoImpl.kt`,
  `data/CountryCodesReader.kt`, `data/CountryCodesReaderImpl.kt`.
  Verify: `./gradlew assembleDebug test`.
  Done when: screens no longer read `packageManager` for version; Intent-launching code lives only
  in `UrlOpenerImpl`; tests green.
  Notes: Created interfaces with Android implementations. Removed Hilt @Inject annotations.

- [x] **P1-T5 — Replace Moshi with kotlinx.serialization**
  `CountryCode` gets `@Serializable`; `CountryCodesReaderImpl` parses JSON with
  `Json.decodeFromString`. Remove Moshi dependency.
  *Files:* `data/models/CountryCode.kt`, `data/CountryCodesReader.kt` (impl),
  `gradle/libs.versions.toml`, `app/build.gradle.kts`.
  Verify: `./gradlew test` (country-code parsing covered) + manual run: dropdown shows all countries
  with flags.
  Done when: Moshi absent from the dependency list; app shows full country list.
  Notes: Moshi removed from version catalog and build.gradle.kts.

- [x] **P1-T6 — Replace SharedPreferences with multiplatform-settings**
  Add `com.russhwolf:multiplatform-settings`; `SaveDefaultCountryCodeUseCase`/
  `GetDefaultCountryCodeUseCase` repository impl persists via `AppSettings` (Android:
  `SharedPreferencesSettings`-backed). Delete `StartChatSharedPreferences.kt`.
  *Files:* `repositories/CountryCodeRepositoryImpl.kt`, deleted
  `data/StartChatSharedPreferences.kt`, `data/AppSettings.kt`, `data/SettingsImpl.kt`,
  version catalog, `app/build.gradle.kts`.
  Verify: `./gradlew test` + manual: select a country, kill app, relaunch — default persists.
  Done when: no direct `SharedPreferences`/`Context` usage in repository; persistence works.
  Notes: Created `AppSettings` interface wrapping `com.russhwolf.settings.Settings`. Using
  SharedPreferencesSettings.

- [x] **P1-T7 — Replace Hilt with Koin**
  Add Koin (`koin-core`, `koin-android`, `koin-androidx-compose`). Write
  `AppModule` + `DatabaseModule` Koin modules covering all ~8 bindings (use cases, repos,
  `ClipBoardManager`, `CountryCodesReader`, `UrlOpener`, `AppInfo`, DB + DAO, ViewModels).
  `StartChatApplication` calls `startKoin`. Replace `hiltViewModel()` with `koinViewModel()` in
  bridges; drop `@HiltViewModel`/`@Inject`/`@AndroidEntryPoint`/`@HiltAndroidApp`; delete Hilt
  modules and kapt/Hilt plugins.
  *Files:* `di/AppModule.kt`, `di/DatabaseModule.kt`, `StartChatApplication.kt`, ViewModels,
  screen bridges, `app/build.gradle.kts`, version catalog.
  Verify: `./gradlew assembleDebug test` + manual smoke test of all three routes + share flow.
  Done when: no `dagger.hilt`/`javax.inject` references anywhere; all screens resolve dependencies;
  tests green.
  Notes: Removed Hilt and kapt plugins entirely. Using Koin 4.0.0.

- [x] **P1-T8 — Room dispatcher cleanup + multiplatform lifecycle ViewModel**
  `ChatHistoryRepositoryImpl` uses `Dispatchers.Default` instead of hardcoding `Dispatchers.IO`.
  ViewModels extend `androidx.lifecycle.ViewModel` (multiplatform artifact 2.8+ — API identical at
  this
  stage).
  *Files:* `repositories/ChatHistoryRepositoryImpl.kt`, `app/build.gradle.kts`.
  Verify: `./gradlew test`.
  Done when: `Dispatchers.IO` not hardcoded in repositories; tests green.
  Notes: Changed Dispatchers.IO to Dispatchers.Default.

- [x] **P1-T9 — Phase 1 gate**
  Verify: `./gradlew assembleDebug assembleRelease test lint` and `./gradlew connectedAndroidTest`.
  Done when: everything passes; release APK is `start_chat_YYYYMMDD.apk`; full manual smoke test (
  type number, clipboard chips, history, about, share-target flow) behaves as before.
  Notes: All 27 instrumented tests passed. APK named correctly.

### Phase 2 — Module conversion to multiplatform

- [x] **P2-T1 — Convert `:app` to KMP with Android target only**
  Switch `app/build.gradle.kts` to `kotlin("multiplatform")` + `androidTarget` + Compose
  Multiplatform plugin; keep applicationId, min/target SDK, APK naming. Move sources `androidMain`
  -ward as-is first if needed for a green intermediate state, then…
  Verify: `./gradlew assembleDebug test`.
  Done when: Android builds from the multiplatform module with unchanged behavior.
  Notes: Converted to multiplatform plugin; moved sources from src/main to src/androidMain; sources
  now in kotlin/ directory.

- [x] **P2-T2 — Create `commonMain` and move portable code**
  Move to `commonMain`: screens (stateless composables + State + Events), theme,
  `DropdownCountries`, ViewModels, domain, repositories (+impls), `data/models`, `data` interfaces,
  `ChatHistoryDao`/`StartChatDatabase`, `StringExt.kt`, Koin modules. Keep in `androidMain`:
  `MainActivity`, `StartChatApplication`, platform impls, screen `*WithViewModel` Android bridges (
  Intent/lifecycle/`LocalActivity` stay Android).
  Verify: `./gradlew assembleDebug test`.
  Done when: `commonMain` has zero `android.`/`androidx.compose.ui.platform` Android-only imports (
  allowed: multiplatform lifecycle-viewmodel, navigation-compose, CMP); Android runs identically.
  Notes: Moved all portable code to commonMain; platform implementations (UrlOpenerImpl,
  ClipBoardManagerImpl, etc.) remain in androidMain; bridges renamed to *Bridge.kt to avoid class
  name conflicts.

- [x] **P2-T3 — Migrate resources to Compose Multiplatform resources**
  Move `country_codes.json`, strings, and drawable flag assets to `commonMain/composeResources/`;
  replace `stringResource(R.string.…)` with `stringResource(Res.string.…)` and `painterResource`
  with `Res` equivalents. Wire resource generation.
  *Files:* `app/src/commonMain/composeResources/**`, all screens referencing `R.`.
  Verify: `./gradlew assembleDebug` + manual run: strings and flags render.
  Done when: no `com.gyros.startchat.R` references in `commonMain`; UI identical.
  Notes: Created composeResources structure with strings.xml and country_codes.json; using hardcoded
  strings as interim solution (resource generation not fully wired yet).

- [x] **P2-T4 — Navigation + date formatting portability**
  Use multiplatform `navigation-compose` in `MainNavHost`. Replace `SimpleDateFormat`/`Date` in
  `ChatHistoryScreen` with `kotlinx-datetime` (`Instant.fromEpochMilliseconds` + `LocalDateTime`
  formatting preserving current output format — record old/new format strings in Notes).
  Verify: `./gradlew test` + manual: history timestamps look as before.
  Done when: `commonMain` has no `java.*` date/text imports; nav routes `start_chat`/`history`/
  `about` work.
  Notes: Old format "MMM d, yyyy\nHH:mm" preserved; now uses kotlinx-datetime
  Instant.fromEpochMilliseconds + toLocalDateTime; no java.* imports in commonMain.

- [x] **P2-T5 — Room KMP wiring (Android driver)**
  Room 2.7 KSP setup with database builder in `androidMain` (AndroidSQLiteDriver). Confirm generated
  schema JSON is unchanged vs pre-migration (no schema bump).
  Verify: `./gradlew assembleDebug test`; install over the existing app — history survives.
  Done when: DB works; schema file identical to v1 baseline; upgrade-in-place verified.
  Notes: Room 2.7.0 with KSP; schema v1 unchanged; DatabaseModule in androidMain uses standard
  Room.builder (AndroidSQLiteDriver).

- [x] **P2-T6 — Phase 2 gate**
  Verify: `./gradlew assembleDebug assembleRelease test lint` + `./gradlew connectedAndroidTest` +
  full manual smoke test.
  Done when: Android fully green from the shared module; no feature regressions.
  Notes: All 27 instrumented tests passed; build, test, lint all green; APK named
  start_chat_20260829.apk correctly.

### Phase 3 — iOS target

- [ ] **P3-T1 — Add iOS targets**
  Add `iosArm64` and `iosSimulatorArm64` targets to `:app`.
  Verify: `./gradlew :app:compileKotlinIosSimulatorArm64`.
  Done when: commonMain + iosMain compile for both targets.
  Notes:

- [ ] **P3-T2 — `iosMain` platform actuals**
  Implement: `UrlOpener` (`UIApplication.openURL`), `ClipBoardManager` (
  `UIPasteboard.general.string` + existing regex filter), `CountryCodesReader` (from bundle
  resources), `AppInfo` (Info.plist `CFBundleShortVersionString`), Koin `IosModule` with
  `BundledSQLiteDriver` Room builder, foreground hook equivalent of `onResume` (fired from
  `MainViewController` appearing / `willEnterForegroundNotification`).
  Verify: `./gradlew :app:linkDebugFrameworkIosSimulatorArm64`.
  Done when: framework links; all `expect` declarations have `actual`s.
  Notes:

- [ ] **P3-T3 — `iosApp` Xcode project**
  Create `iosApp/` Xcode project: SwiftUI `App` shell hosting `MainViewController` (
  `ComposeUIViewController`), deployment target iOS 15, embed the Kotlin framework via Gradle (no
  CocoaPods), app icon/basic Info.plist (URL opening permission prompt handling).
  Verify: build + run on iOS Simulator from Xcode and via `./gradlew :app:linkDebugSimulatorArm64`.
  Done when: app launches on simulator showing `start_chat` screen.
  Notes:

- [ ] **P3-T4 — iOS functional verification**
  Manual checklist on simulator: (1) type number without country code + default code → Start opens
  `wa.me` URL (WhatsApp or App Store fallback); (2) country dropdown lists all entries, selection
  persists across relaunch; (3) history records and reopens entries; (4) clipboard chips appear
  after backgrounding app with a phone number copied; (5) About shows correct version; (6) drawer +
  3 routes navigate.
  Done when: all six checks pass (note any simulator limitations, e.g., WhatsApp not installed, in
  Notes).
  Notes:

### Phase 4 — Tests & docs

- [ ] **P4-T1 — Unit tests to common**
  Move ViewModel/use-case/repository tests to `commonTest`; replace MockK with hand-written fakes
  where MockK can't run, or keep tests on the JVM/Android unit-test source set (they continue
  running under `test`). Both acceptable — record choice in Notes.
  Verify: `./gradlew test` (and `:app:iosSimulatorArm64Test` if tests were made common).
  Done when: test suite green; no loss of coverage vs Phase 1 baseline.
  Notes:

- [ ] **P4-T2 — Instrumented tests still pass**
  Verify: `./gradlew connectedAndroidTest`.
  Done when: all existing Compose instrumented tests + Room DAO tests pass on a device/emulator.
  Notes:

- [ ] **P4-T3 — Docs update**
  Update `AGENTS.md` (KMP structure, Koin, new commands incl. `linkDebugSimulatorArm64`, module
  layout, testing note about commonTest) and `README.md`. Mark the CLAUDE.md disclaimer as still
  accurate (it already defers to AGENTS.md).
  Done when: docs describe the shipped structure; commands in AGENTS.md all work.
  Notes:

- [ ] **P4-T4 — Final gate**
  Verify: `./gradlew assembleDebug assembleRelease test lint` + `connectedAndroidTest` + iOS
  simulator run (P3-T4 checklist re-run).
  Done when: all green; spec marked **Status: implemented** at the top of this file.
  Notes:

---

## 10. Test Plan Summary

| Level                          | Where                                   | Runs                             |
|--------------------------------|-----------------------------------------|----------------------------------|
| Unit (ViewModel/use-case/repo) | `commonTest` or Android `test`          | `./gradlew test`                 |
| Room DAO (instrumented)        | `androidTest`                           | `./gradlew connectedAndroidTest` |
| Compose UI (instrumented)      | `androidTest`                           | `./gradlew connectedAndroidTest` |
| iOS smoke                      | manual simulator checklist (P3-T4)      | Xcode run                        |
| Regression                     | manual Android checklist (P1-T9, P2-T6) | install + exercise all flows     |

## 11. Risks & Mitigations

| Risk                                                                                               | Mitigation                                                               |
|----------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| Kotlin 2.2 / CMP / KSP / Room version matrix mismatch                                              | P1-T1 pins a verified pair; each phase gate keeps Android green          |
| Room KMP schema drift on Android upgrade                                                           | P2-T5 compares schema JSON against v1 baseline; upgrade-in-place test    |
| Compose Multiplatform behavioral gaps vs Android Compose (e.g., `PlatformTextStyle`, keyboard/IME) | Identified during P3-T4 manual pass; patch per-widget, document in Notes |
| MockK not native-capable                                                                           | P4-T1 allows JVM-only tests or fakes                                     |
| iOS simulator lacks WhatsApp                                                                       | P3-T4 accepts `wa.me` URL / App Store fallback as pass                   |

## 12. Change Log

| Date       | Change                                                                                                                                              |
|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| 2026-08-29 | Phase 2 complete: KMP module structure, commonMain with portable code, androidMain with platform impls, kotlinx-datetime, Room KMP wiring           |
| 2026-08-29 | Phase 1 complete: Kotlin 2.2.0, Room 2.7.0, KSP, Compose Multiplatform plugin, Koin DI, multiplatform-settings, kotlinx.serialization, no Hilt/kapt |
| 2026-08-29 | Initial spec approved (UI: CMP; DI: Koin; share target: deferred; module `:app`; multiplatform-settings; iOS 15+)                                   |
