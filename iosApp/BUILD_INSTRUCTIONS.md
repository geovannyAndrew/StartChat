# StartChat iOS Build Instructions

## Prerequisites

1. **Java 17+** must be installed and available in PATH
2. **Xcode 15+** with iOS Simulator support
3. **Kotlin 2.2.0** (managed by Gradle)

## Build Steps

### 1. Build the Kotlin Framework

First, build the Kotlin framework for iOS simulator using Gradle:

```bash
cd /path/to/StartChat
./gradlew :app:compileKotlinIosSimulatorArm64
./gradlew :app:linkDebugFrameworkIosSimulatorArm64
```

This produces the Kotlin framework at:
`app/build/bin/iosSimulatorArm64/debugFramework/StartChat.framework`

### 2. Open in Xcode

Open the Xcode project:

```bash
open iosApp/StartChat.xcodeproj
```

### 3. Build and Run

1. Select an iOS Simulator (iPhone 15 or similar)
2. Press Cmd+R to build and run

Or from command line:

```bash
xcodebuild -project iosApp/StartChat.xcodeproj \
  -scheme StartChat \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 15,OS=17.5' \
  CODE_SIGNING_ALLOWED=NO \
  build
```

> `CODE_SIGNING_ALLOWED=NO` skips code signing, which is not enforced on the simulator.
> The project has no development team configured; Xcode's GUI "Run" will complain about
> signing for the ShareExtension target — build from the command line instead, or add a
> Personal Team in Xcode → Settings → Accounts and select it for both targets.
> If you get "Unable to find a device matching the provided destination specifier", your
> simulator list has duplicates — find one with `xcrun simctl list devices available` and
> disambiguate with `OS=` or use its `id=`.

### 4. Running the App

The app launches showing the `start_chat` screen. Since this is a KMP app:

- The Kotlin framework contains all shared business logic
- SwiftUI hosts the Compose UI via `ComposeUIViewController`
- Default country code persistence uses in-memory settings (for v1)

## Troubleshooting

### "Framework not found" errors

Ensure step 1 (Gradle build) completed successfully before opening in Xcode.

### "MainViewController not found"

The `MainViewController` class is defined in Kotlin in `iosMain`. It must be compiled into the
Kotlin framework by Gradle before Xcode can find it.

### iOS Simulator not available

Check available simulators:

```bash
xcrun simctl list devices available
```
