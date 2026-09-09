# Spec: StartChat — iOS Share Extension (ACTION_SEND parity)

- **Status:** implemented
- **Date:** 2026-08-29
- **Prerequisite:** `docs/kmp-migration-spec.md` must be **Status: implemented** (its non-goal "iOS
  Share Extension" is covered by this spec).
- **Workflow:** Spec-Driven Development (SSD). Implement tasks strictly in order. A task is only
  marked complete `[x]` after its **Done when** checks pass. Do not start the next task until the
  current one is checked off.

---

## 1. Mission

Give iOS users the Android `ACTION_SEND` experience: share text from any app via the iOS share sheet
into StartChat, which then sanitizes it, saves history, and opens the WhatsApp chat — using the
shared Kotlin pipeline as the single source of truth.

## 2. Current State (post-migration baseline)

- KMP app (`:app`): `commonMain` holds ViewModels/domain/repos; `iosMain` has platform actuals and a
  foreground hook (clipboard-on-resume equivalent, migration spec P3-T2).
- `StartChatViewModel.start(actionText: String?)` already implements the full Android share flow:
  `processText` → sanitize → `hasCountryCode` → build `wa.me` URL → save history → emit
  `Events.StartIntentAction` → `UrlOpener.open`. Invalid text lands in the input field.
- `iosApp/` Xcode project hosts the Compose UI (SwiftUI App shell).
- **iOS platform constraint that shapes this design:** Apple does not support opening URLs from a
  Share Extension (`NSExtensionContext.open` is unsupported for share extensions; `UIApplication` is
  unavailable in extension processes). The extension therefore cannot open `wa.me` — or the
  containing app — directly.

## 3. Goals

- G1: StartChat appears in the iOS share sheet for single-item plain-text (and URL-as-text) shares.
- G2: Confirmed shared text is processed by the existing shared Kotlin pipeline (
  `start(actionText)`) with Android parity: valid number → `wa.me` open + history saved; invalid →
  text shown in the input field.
- G3: Extension is tiny (SwiftUI only), stable under the ~120 MB extension memory cap, and contains
  no phone-number logic.
- G4: Zero impact on the Android app (no code or behavior changes outside `iosMain`/`iosApp`).

## 4. Non-Goals

- Opening WhatsApp (or the containing app) directly from the extension — impossible per Apple
  rules (see §2); rejected workarounds below.
- App Store submission, signing, and provisioning beyond automatic signing on simulator.
- Sharing images, files, or multiple items.
- Android-side changes of any kind.

## 5. Locked Decisions

| ID  | Decision                                                                                                                                                                                                                                                                                                                            |
|-----|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| D1  | **Extension UI: SwiftUI**, hosted in a plain `UIViewController` (not `SLComposeServiceViewController`, not Compose). The Kotlin framework is **not** linked into the appex — keeps the extension small and avoids duplicating the multi-hundred-MB Compose framework into the extension binary.                                     |
| D2  | **Handoff: App Group shared UserDefaults** (`group.com.gyros.startchat`). Extension writes raw shared text + timestamp; the containing app processes it on next activation.                                                                                                                                                         |
| D3  | **Rejected alternatives** (documented, not implemented): (a) `NSExtensionContext.open` — unsupported for share extensions; (b) responder-chain `openURL:` private-API hack — works in practice but risks App Store rejection; (c) linking the Kotlin framework into the appex — size/memory cost not justified by ~30 LOC of logic. |
| D4  | **Phone logic lives only in shared Kotlin.** The extension is a logic-free pass-through showing the raw text; all sanitizing/validation happens via `start(actionText)` in the app. Single source of truth, no Swift/Kotlin drift.                                                                                                  |
| D5  | **Pending text TTL: 10 minutes.** Stale entries are ignored and cleared (prevents yesterday's share suddenly launching WhatsApp).                                                                                                                                                                                                   |
| D6  | **Exactly-once semantics:** the pending entry is removed from the store *before* processing.                                                                                                                                                                                                                                        |
| D7  | **Activation rule:** exactly 1 item, types `public.utf8-plain-text` and `public.url` (URL shared as its `absoluteString`). Mirrors Android `ACTION_SEND text/*`.                                                                                                                                                                    |
| D8  | Extension target lives inside the existing `iosApp` Xcode project; bundle id `com.gyros.startchat.share`. App Group entitlement added to both app and appex targets (works on simulator with automatic signing).                                                                                                                    |
| D9  | Store keys are a documented contract between Swift and Kotlin: `pendingSharedText` (String) and `pendingSharedTimestamp` (epoch ms as Long/Int64).                                                                                                                                                                                  |
| D10 | Reuse `start(actionText)` — no new use case on the Kotlin side; only a new `PendingSharedTextStore` in `iosMain`.                                                                                                                                                                                                                   |

## 6. Target Design

```
┌──────────────┐  1. share sheet    ┌─────────────────────────┐
│ Host app     │ ─────────────────► │ ShareExtension (appex)  │
│ (Safari/…)   │                    │ SwiftUI preview +       │
└──────────────┘                    │ "Open in StartChat" btn │
                                    └───────────┬─────────────┘
                                                │ 2. write raw text +
                                                │    timestamp (App Group)
                                                ▼
                          ┌──────────────────────────────────────┐
                          │ App Group UserDefaults               │
                          │  pendingSharedText / …Timestamp       │
                          └──────────────────┬───────────────────┘
                                             │ 3. read on app-active
                                             ▼
┌────────────────────────────────────────────────────────────────┐
│ StartChat app (iosMain)                                        │
│ foreground hook (existing, extended):                          │
│   if pending text fresh (≤10 min) → clear → start(text)        │
│   → StartChatViewModel.processText (shared Kotlin, unchanged)  │
│     → valid: wa.me open + history save                         │
│     → invalid: text lands in input field                       │
└────────────────────────────────────────────────────────────────┘
```

Files touched:

- `app/src/iosMain/kotlin/…/data/PendingSharedTextStore.kt` (new) — read/write/clear + TTL, `Clock`
  injected for testability.
- `app/src/iosTest/kotlin/…/PendingSharedTextStoreTest.kt` (new).
- `app/src/iosMain/kotlin/…/MainViewController.kt` or the SwiftUI App shell (extend existing
  foreground hook) + routing of pending text into the `start_chat` ViewModel's `start()`.
- `iosApp/` — new `ShareExtension` target: `ShareViewController.swift` (UI + `NSItemProvider`
  text/URL extraction), `ShareExtension.entitlements`, activation rule in Info.plist; App Group
  entitlement on both targets.

## 7. Requirements

### Functional

- FR1: StartChat appears in the iOS share sheet when sharing exactly one text or URL item.
- FR2: The extension shows the shared text (truncated preview) and an "Open in StartChat" action;
  confirming writes the raw text + timestamp to the App Group and completes the request.
- FR3: On next app activation (cold start or foreground), a fresh pending entry (≤10 min) is routed
  through `start(actionText)` with Android parity (valid → WhatsApp opens + history recorded;
  invalid → text in input field).
- FR4: A pending entry is processed at most once and never when stale; stale entries are silently
  cleared.
- FR5: Processing works regardless of the active route (`start_chat`, `history`, `about`) — the
  `wa.me` open is an app-level side effect via `UrlOpener`.
- FR6: Declining/cancelling the extension leaves no pending entry.

### Technical

- TR1: No phone-number parsing/sanitizing logic in Swift (D4).
- TR2: Kotlin framework not linked into the appex (D1).
- TR3: Store contract per D9 — key names in exactly one documented place; a constant on the Kotlin
  side and in `ShareViewController.swift`.
- TR4: TTL injectable (`kotlinx-datetime` `Clock`) so unit tests can time-travel.
- TR5: Android build untouched: `./gradlew assembleDebug test lint` unchanged before/after this
  spec.
- TR6: App Group id `group.com.gyros.startchat` declared in both targets' entitlements.

## 8. Constraints

- Each phase must leave both apps buildable (Android verified by TR5).
- Share Extensions run under a strict memory cap — keep the extension SwiftUI-only and
  dependency-free.
- Xcode/simulator available in this environment; real-device provisioning for App Groups is out of
  scope (automatic signing only).

---

## 9. Task Breakdown

> **Rules:** work strictly top-to-bottom; run the task's **Verify** steps; tick `[x]` only when all
**Done when** conditions hold. Notes go in the task's `Notes:` line (append, don't delete).

### Phase 1 — App-side pending-text pipeline (no extension yet)

- [ ] **SX-T1 — `PendingSharedTextStore` in `iosMain`**
  New class reading/writing/clearing the App Group UserDefaults per D9 keys:
  `read(): PendingSharedText?` (text + timestamp), `write(text)`, `clear()`,
  `takeIfFresh(maxAgeMs = 10 min, now): PendingSharedText?` which returns and **removes** a fresh
  entry (D6). Constructor takes the `UserDefaults(suiteName:)` and a `kotlinx-datetime` `Clock`.
  *Files:* `app/src/iosMain/…/data/PendingSharedTextStore.kt`,
  `app/src/iosTest/…/PendingSharedTextStoreTest.kt`.
  Verify: `./gradlew :app:iosSimulatorArm64Test`.
  Done when: tests cover round-trip, fresh-read-removes-entry, stale-entry-returns-null-and-clears,
  empty-returns-null; all pass.
  Notes:

- [ ] **SX-T2 — Foreground hook wiring**
  Extend the existing iOS foreground/active hook (migration spec P3-T2): on every app activation (
  first launch **and** resume), call `takeIfFresh`; if non-null, route the text into the
  `start_chat` ViewModel's `start(actionText)` (mechanism: shell-level state passed down, or an
  app-scope Koin-scoped event bus — implementer's choice, record in Notes). Must work from any
  route (FR5) and not double-process (D6 guarantees this).
  *Files:* `MainViewController.kt` / SwiftUI App shell, start-chat screen wiring.
  Verify: `./gradlew :app:compileKotlinIosSimulatorArm64` +
  `./gradlew :app:linkDebugSimulatorArm64`; full E2E deferred to SX-T5.
  Done when: iOS app builds and runs; code review confirms first-launch + resume paths both check
  the store; pending text reaches `start()`.
  Notes:

### Phase 2 — Share Extension target

- [ ] **SX-T3 — appex target, entitlements, activation rules**
  Add `ShareExtension` target to the `iosApp` Xcode project (bundle id `com.gyros.startchat.share`,
  D8). Info.plist `NSExtensionActivationRule`: `NSExtensionActivationSupportsText = true` (max 1) +
  URL support per D7 — use a dict rule or `NSPredicate` ("SUBQUERY … count == 1"), record the chosen
  rule in Notes. Add App Group `group.com.gyros.startchat` entitlement to **both** app and appex
  targets. Extension compiles with a placeholder UI.
  *Files:* `iosApp/ShareExtension/*` (Swift, Info.plist, entitlements), app target entitlements.
  Verify: build in Xcode; share a text selection from Notes on simulator → StartChat appears in the
  share sheet.
  Done when: extension visible and selectable in the share sheet; both targets have the App Group
  capability.
  Notes:

- [ ] **SX-T4 — Extension UI + text extraction + handoff write**
  `ShareViewController` (SwiftUI hosted): load the single item via `NSItemProvider` (
  `UTType.plainText` fallback `UTType.url` → `absoluteString`), show a truncated preview, "Open in
  StartChat" button enabled when text was loaded. On tap: write `pendingSharedText` +
  `pendingSharedTimestamp` (epoch ms) to `UserDefaults(suiteName: "group.com.gyros.startchat")`
  using the D9 keys, then `completeRequest`. Cancel path writes nothing (FR6). Failed text load
  shows an error label with the action disabled.
  *Files:* `iosApp/ShareExtension/ShareViewController.swift` (+ SwiftUI view).
  Verify: on simulator, share a phone number from Notes → confirm → inspect App Group container (
  `xcrun simctl spawn booted defaults read group.com.gyros.startchat` or a debug breakpoint) shows
  both keys.
  Done when: keys written exactly per contract; cancel writes nothing; UI handles
  missing/unsupported items gracefully.
  Notes:

### Phase 3 — End-to-end & gates

- [ ] **SX-T5 — E2E verification checklist (simulator)**
  Manual checks: (1) valid number with country code → confirm → open StartChat → WhatsApp URL / App
  Store fallback opens, entry in history; (2) valid number without country code → default code
  applied (Android parity); (3) invalid text → app opens, text in input field, Start disabled; (4)
  cold start: kill app after confirming in extension, then launch → pending still processed; (5)
  stale: write a >10-min-old entry (temporarily via test hook or unit-test aid) → ignored and
  cleared; (6) confirm while app sits on `history`/`about` route → wa.me still opens; (7) confirm
  twice without opening app → processed once, latest text wins.
  Done when: all seven checks pass; deviations recorded in Notes.
  Notes:

- [ ] **SX-T6 — Android regression gate, docs, spec closure**
  Verify: `./gradlew assembleDebug assembleRelease test lint` (TR5 — must match the migration spec
  P4-T4 baseline) + Android manual smoke test unchanged.
  Docs: update `AGENTS.md` and `README.md` — new `iosApp/ShareExtension` target, App Group id, store
  contract keys, TTL, how to test the extension on simulator. Cross-reference this spec from the
  migration spec's Non-Goals section.
  Done when: Android fully green; docs accurate; **Status** at the top of this file set to
  `implemented`.
  Notes:

---

## 10. Test Plan Summary

| Level                                            | Where                                | Runs                                                |
|--------------------------------------------------|--------------------------------------|-----------------------------------------------------|
| Store unit tests (round-trip, TTL, exactly-once) | `app/src/iosTest`                    | `./gradlew :app:iosSimulatorArm64Test`              |
| Extension behavior                               | manual share-sheet checks (SX-T3/T4) | Xcode + simulator                                   |
| E2E parity with Android `ACTION_SEND`            | manual checklist (SX-T5)             | simulator                                           |
| Android regression                               | full gate (SX-T6)                    | `./gradlew assembleDebug assembleRelease test lint` |

## 11. Risks & Mitigations

| Risk                                                       | Mitigation                                                                                            |
|------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| Extension cannot open the app directly (Apple restriction) | Design bakes this in: App Group handoff + app-side processing (D2); hack rejected with rationale (D3) |
| Share sheet doesn't show the extension                     | Activation-rule dict verified in SX-T3; rule recorded in Notes for debugging                          |
| Extension memory cap exceeded                              | SwiftUI-only, no Kotlin/Compose in appex (D1/TR2)                                                     |
| Key-name drift between Swift and Kotlin                    | D9 contract documented in both specs; SX-T4 verifies actual written keys                              |
| Double-processing on repeated activations                  | `takeIfFresh` removes-before-return (D6); SX-T5 check 7                                               |
| Stale share launching WhatsApp unexpectedly                | 10-min TTL (D5); SX-T5 check 5                                                                        |
| App Group provisioning fails on real devices               | Out of scope (simulator/automatic signing only, §8); documented in SX-T6                              |

## 12. Change Log

| Date       | Change                                                                                                             |
|------------|--------------------------------------------------------------------------------------------------------------------|
| 2026-08-29 | Initial spec: App Group handoff design (D2), SwiftUI-only extension (D1), 10-min TTL (D5), Android untouched (TR5) |
