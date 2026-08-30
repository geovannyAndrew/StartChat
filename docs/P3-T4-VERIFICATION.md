# P3-T4: iOS Functional Verification Checklist

## Prerequisites

- [ ] Java 17+ installed
- [ ] Kotlin framework built:
  `./gradlew :app:compileKotlinIosSimulatorArm64 :app:linkDebugFrameworkIosSimulatorArm64`
- [ ] App installed on iOS Simulator (iOS 15+)

---

## Manual Test Checklist

### 1. Start Chat Flow (FR1, FR3, FR9)

- [ ] **1.1** Type a phone number (e.g., `1234567890`) without country code
  - Expected: Default country code is prepended (if previously selected) OR no prefix
- [ ] **1.2** Tap "Start Chat" button
  - Expected: `wa.me/...` URL is opened (WhatsApp or App Store fallback on simulator)
- [ ] **1.3** Select a country code from dropdown, type a number, tap Start Chat
  - Expected: `wa.me/<countrycode><number>` URL opened

### 2. Country Code Persistence (FR9)

- [ ] **2.1** Select a country code (e.g., United States +1)
- [ ] **2.2** Kill the app (swipe up from app switcher)
- [ ] **2.3** Re-launch the app
- [ ] **2.4** Verify the previously selected country code is still selected
  - **Note:** v1 uses MemorySettings, so persistence may NOT work. This is expected for Phase 3.

### 3. History Screen (FR4, FR5, FR8)

- [ ] **3.1** Open drawer/menu, tap "History"
- [ ] **3.2** Verify screen shows (empty initially or with previous entries)
- [ ] **3.3** Start a chat, verify it appears in history
- [ ] **3.4** Tap a history entry
  - Expected: Opens WhatsApp with that number

### 4. Clipboard Chips (FR6)

- [ ] **4.1** Copy a valid phone number to clipboard (e.g., `9876543210`)
- [ ] **4.2** Background the app (press home or switch apps)
- [ ] **4.3** Foreground the app
- [ ] **4.4** Verify clipboard chip appears above the text field
  - **Note:** iOS clipboard access is more restricted than Android

### 5. About Screen (FR7)

- [ ] **5.1** Open drawer/menu, tap "About"
- [ ] **5.2** Verify app name "Start Chat" is shown
- [ ] **5.3** Verify version is displayed (from Info.plist CFBundleShortVersionString)

### 6. Navigation (FR8)

- [ ] **6.1** Open drawer, verify 3 routes: start_chat, history, about
- [ ] **6.2** Navigate between all three screens
- [ ] **6.3** Verify back navigation works

---

## Simulator Limitations

- WhatsApp is not installed on iOS Simulator
- URLs to `wa.me/<number>` will open Safari or show "Cannot Open Page"
- This is expected behavior - the URL is still correctly formed

---

## Pass Criteria

All 6 checks must pass. Any failures should be documented in the Notes section of P3-T4 in
`kmp-migration-spec.md`.

## Notes

(To be filled after manual testing)
