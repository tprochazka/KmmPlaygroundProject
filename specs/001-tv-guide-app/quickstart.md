# Quick Start Guide

**Feature**: TV Program Guide Application  
**Date**: 2025-11-02  
**Phase**: 1 - Prototype Setup

## Overview

This guide helps developers set up and run the TV Program Guide application prototype on all supported platforms (Android, iOS, Web, Desktop).

## Prerequisites

### Required Tools

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| **JDK** | 17 | Kotlin compilation, Android builds |
| **Android Studio** | Koala (2024.1.1) | Android development, KMP support |
| **Xcode** | 15.0 | iOS builds (macOS only) |
| **Kotlin** | 2.2.21 | Language runtime |
| **Gradle** | 8.5 | Build system |

### Platform-Specific Prerequisites

#### Android
- Android SDK 24+ (Android 7.0 Nougat)
- Android emulator or physical device
- USB debugging enabled (for physical device)

#### iOS (macOS only)
- macOS 13.0+ (Ventura or later)
- Xcode 15.0+
- iOS Simulator or physical device
- Apple Developer account (for physical device deployment)
- CocoaPods: `sudo gem install cocoapods`

#### Web (Wasm/JS)
- Modern browser (Chrome 119+, Firefox 120+, Safari 17+)
- Node.js 18+ (for Webpack dev server)

#### Desktop (JVM)
- JDK 17+
- Windows 10+, macOS 13+, or Linux with X11/Wayland

## Project Setup

### 1. Clone Repository

```powershell
# Clone the repository
git clone https://github.com/your-org/tv-guide-kmm.git
cd tv-guide-kmm

# Checkout the development branch (if not on master)
# git checkout develop
```

### 2. Verify Gradle Wrapper

```powershell
# Verify Gradle wrapper is executable
./gradlew --version

# Expected output:
# Gradle 8.5
# Kotlin: 2.2.21
```

### 3. Sync Project Dependencies

```powershell
# Download and sync all dependencies
./gradlew clean build --refresh-dependencies

# This will:
# - Download Kotlin Multiplatform libraries
# - Sync Compose Multiplatform dependencies
# - Set up platform-specific toolchains
# - Build all modules
```

## Running the Application

### Android

#### Option 1: Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select `composeApp` run configuration
4. Choose Android device/emulator
5. Click Run button (Shift+F10)

#### Option 2: Command Line

```powershell
# List available devices
./gradlew :composeApp:installDebug

# Run on connected device
adb shell am start -n com.tvguide.app/com.tvguide.app.MainActivity

# Or use Gradle task
./gradlew :composeApp:installDebugAndroidTest
```

#### Option 3: Build APK

```powershell
# Build debug APK
./gradlew :composeApp:assembleDebug

# APK location: composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Install manually
adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### iOS

#### Option 1: Xcode

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select target device (Simulator or physical device)
3. Click Run button (Cmd+R)
4. Wait for Kotlin/Native compilation and app launch

#### Option 2: Command Line (Simulator)

```powershell
# Build iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Open Xcode project
open iosApp/iosApp.xcodeproj

# Or use xcodebuild
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -destination 'platform=iOS Simulator,name=iPhone 15 Pro' \
           -configuration Debug \
           build
```

#### Troubleshooting iOS Builds

```powershell
# Clean Kotlin/Native cache if build fails
./gradlew cleanNativeCache

# Rebuild iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 --rerun-tasks

# Clean Xcode derived data
rm -rf ~/Library/Developer/Xcode/DerivedData
```

### Web (Wasm)

#### Development Server

```powershell
# Start Webpack dev server with hot reload
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Server starts at http://localhost:8080
# Open browser and navigate to http://localhost:8080
```

#### Production Build

```powershell
# Build optimized Wasm bundle
./gradlew :composeApp:wasmJsBrowserDistribution

# Output: composeApp/build/dist/wasmJs/productionExecutable/

# Serve production build (requires Python 3)
cd composeApp/build/dist/wasmJs/productionExecutable
python -m http.server 8000

# Navigate to http://localhost:8000
```

#### Web (JS Fallback)

```powershell
# For browsers without Wasm support
./gradlew :composeApp:jsBrowserDevelopmentRun

# Production build
./gradlew :composeApp:jsBrowserDistribution
```

### Desktop (JVM)

#### Option 1: Gradle Run Task

```powershell
# Run desktop application
./gradlew :composeApp:run

# Application window opens
```

#### Option 2: Package Executable

```powershell
# Create distributable package
./gradlew :composeApp:packageDistributionForCurrentOS

# Output locations:
# Windows: composeApp/build/compose/binaries/main/msi/
# macOS: composeApp/build/compose/binaries/main/dmg/
# Linux: composeApp/build/compose/binaries/main/deb/
```

#### Option 3: Run JAR

```powershell
# Build JAR
./gradlew :composeApp:jvmJar

# Run JAR
java -jar composeApp/build/libs/composeApp-jvm-1.0.0.jar
```

## Development Workflow

### Hot Reload

| Platform | Hot Reload Support | Command |
|----------|-------------------|---------|
| **Android** | ✅ Yes (Live Edit) | Android Studio: Apply Changes |
| **iOS** | ⚠️ Partial (SwiftUI preview) | Xcode: Canvas preview |
| **Web (Wasm)** | ✅ Yes (Webpack HMR) | Automatic on file save |
| **Desktop** | ⚠️ Restart required | Rerun `./gradlew :composeApp:run` |

### Code Quality Checks

```powershell
# Run Kotlin lint
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat

# Run detekt static analysis
./gradlew detekt

# Run all checks
./gradlew check
```

### Testing

```powershell
# Run common tests
./gradlew :composeApp:cleanAllTests :composeApp:allTests

# Platform-specific tests
./gradlew :composeApp:testDebugUnitTest           # Android
./gradlew :composeApp:iosSimulatorArm64Test       # iOS
./gradlew :composeApp:jsTest                      # JS
./gradlew :composeApp:wasmJsTest                  # Wasm
./gradlew :composeApp:jvmTest                     # Desktop

# Generate test coverage report
./gradlew koverHtmlReport

# Report location: composeApp/build/reports/kover/html/index.html
```

### Database Inspection

#### Android

```powershell
# Pull database from device
adb pull /data/data/com.tvguide.app/databases/tvguide.db ./tvguide.db

# Inspect with SQLite
sqlite3 tvguide.db
> .tables
> SELECT * FROM channels;
```

#### Desktop

```powershell
# Database location (Windows)
# C:\Users\<username>\AppData\Roaming\TvGuide\databases\tvguide.db

# Database location (macOS)
# ~/Library/Application Support/TvGuide/databases/tvguide.db

# Database location (Linux)
# ~/.local/share/TvGuide/databases/tvguide.db

# Inspect
sqlite3 <path-to-db>
```

## Project Structure

```
KmmPlaygroundProject/
├── composeApp/                    # Shared Compose UI module
│   ├── src/
│   │   ├── commonMain/           # Shared code
│   │   │   ├── kotlin/
│   │   │   │   ├── domain/       # Use cases, entities
│   │   │   │   ├── data/         # Repositories, data sources
│   │   │   │   └── presentation/ # ViewModels, screens
│   │   │   └── resources/        # Shared resources
│   │   ├── androidMain/          # Android-specific code
│   │   ├── iosMain/              # iOS-specific code
│   │   ├── jvmMain/              # Desktop-specific code
│   │   ├── wasmJsMain/           # Wasm-specific code
│   │   └── jsMain/               # JS-specific code (fallback)
│   └── build.gradle.kts          # Module build config
├── iosApp/                        # iOS app entry point
│   ├── iosApp/
│   │   └── iOSApp.swift          # SwiftUI app wrapper
│   └── iosApp.xcodeproj/
├── gradle/
│   └── libs.versions.toml        # Centralized dependency versions
├── build.gradle.kts               # Root build script
└── settings.gradle.kts            # Project settings
```

## Mock Data

Phase 1 uses mocked data at the repository layer. To modify mock data:

### Location

```kotlin
// File: composeApp/src/commonMain/kotlin/data/mock/MockData.kt

val mockChannels = channels {
    channel {
        id = "ct1"
        name = "ČT1"
        logoUrl = "https://example.com/ct1.png"
        category = ChannelCategory.NATIONAL
        number = 1
    }
    // Add more channels...
}
```

### Adding Mock Programs

```kotlin
val mockPrograms = programs {
    program {
        id = "p1"
        title = "Zprávy"
        channelId = "ct1"
        startTime = Clock.System.now()
        endTime = Clock.System.now() + 30.minutes
        type = ProgramType.NEWS
        description = "Hlavní zpravodajský pořad"
    }
    // Add more programs...
}
```

### Simulated Delays

Adjust network simulation in `MockProgramRepository.kt`:

```kotlin
override suspend fun getCurrentPrograms(channelIds: List<String>): List<Program> {
    delay(300..800) // Change this range to simulate faster/slower network
    return mockPrograms.filter { /* ... */ }
}
```

## Configuration

### Gradle Properties

Edit `gradle.properties` to customize build:

```properties
# Kotlin settings
kotlin.code.style=official
kotlin.js.compiler=ir

# KMP settings
kotlin.mpp.stability.nowarn=true
kotlin.mpp.androidSourceSetLayoutVersion=2

# Android settings
android.useAndroidX=true
android.nonTransitiveRClass=true

# Build settings
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
org.gradle.parallel=true
org.gradle.caching=true
```

### Application ID

Change package name in `composeApp/build.gradle.kts`:

```kotlin
android {
    namespace = "com.tvguide.app" // Change this
    applicationId = "com.tvguide.app" // And this
}
```

## Troubleshooting

### Common Issues

#### Issue: Gradle Sync Failed

```powershell
# Clear Gradle caches
./gradlew clean
rm -rf .gradle/
rm -rf build/

# Re-sync
./gradlew build --refresh-dependencies
```

#### Issue: Android Build Failed - "SDK not found"

```powershell
# Create/edit local.properties
echo "sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk" > local.properties

# Or set ANDROID_HOME environment variable
$env:ANDROID_HOME = "C:\Users\<username>\AppData\Local\Android\Sdk"
```

#### Issue: iOS Build Failed - "Framework not found"

```powershell
# Rebuild iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 --rerun-tasks

# Clean Xcode derived data
rm -rf ~/Library/Developer/Xcode/DerivedData

# Rebuild in Xcode
# Product > Clean Build Folder (Shift+Cmd+K)
# Product > Build (Cmd+B)
```

#### Issue: Wasm Build Failed - "Out of memory"

```powershell
# Increase Gradle heap size in gradle.properties
org.gradle.jvmargs=-Xmx6g -XX:MaxMetaspaceSize=2g

# Or pass as command line argument
./gradlew :composeApp:wasmJsBrowserDevelopmentRun -Dorg.gradle.jvmargs=-Xmx6g
```

#### Issue: Desktop App Won't Launch

```powershell
# Check Java version
java -version
# Should be 17+

# Try running with explicit JDK
JAVA_HOME=/path/to/jdk17 ./gradlew :composeApp:run
```

### Logging

Enable debug logging to troubleshoot issues:

```kotlin
// In commonMain App.kt
object AppLogger {
    init {
        Logger.setMinSeverity(Severity.Debug) // Change to Debug
    }
}
```

View logs:

```powershell
# Android
adb logcat -s TVGuide

# iOS (in Xcode)
# View > Debug Area > Activate Console

# Desktop
# Logs printed to console where app was launched
```

## Next Steps

After successful setup:

1. **Explore codebase**: Start with `composeApp/src/commonMain/kotlin/App.kt`
2. **Run on all platforms**: Verify app works on Android, iOS, Web, Desktop
3. **Modify mock data**: Add channels/programs in `MockData.kt`
4. **Review architecture**: Study `domain/`, `data/`, `presentation/` layers
5. **Check spec**: Read `specs/001-tv-guide-app/spec.md` for feature requirements
6. **Follow plan**: Review `specs/001-tv-guide-app/plan.md` for iteration roadmap

## Resources

- **Kotlin Multiplatform**: https://kotlinlang.org/docs/multiplatform.html
- **Compose Multiplatform**: https://www.jetbrains.com/lp/compose-multiplatform/
- **Room KMP**: https://developer.android.com/kotlin/multiplatform/room
- **Coil 3**: https://coil-kt.github.io/coil/
- **Voyager**: https://voyager.adriel.cafe/
- **Metro DI**: https://github.com/ZacSweers/metro

## Support

For issues or questions:

1. Check this guide first
2. Review [Troubleshooting](#troubleshooting) section
3. Search project issues on GitHub
4. Ask in team Slack channel #tv-guide-dev
5. Create GitHub issue with reproduction steps
