# SoothSayer Setup Guide

## Quick Start

### Prerequisites
Before you begin, ensure you have the following installed:

-  **Android Studio** (Arctic Fox 00.. or later)
-  **JDK ** or higher
-  **Git** for version control
-  **Android SDK 4+** (Android 7.0 or higher)

### Step : Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/soothsayer-predictor.git
cd soothsayer-predictor
```

### Step : Open in Android Studio

. Launch Android Studio
. Click **File** → **Open**
. Navigate to the cloned `soothsayer-predictor` directory
4. Click **OK**

### Step : Sync Gradle

Android Studio will automatically detect the Gradle configuration and prompt you to sync. Click **Sync Now**.

Wait for Gradle to download all dependencies (this may take a few minutes on first run).

### Step 4: Configure API Keys (Optional)

Create a `local.properties` file in the root directory:

```properties
# Optional: Add API keys for higher rate limits
binance.api.key=YOUR_BINANCE_API_KEY
coingecko.api.key=YOUR_COINGECKO_API_KEY
cryptocompare.api.key=YOUR_CRYPTOCOMPARE_API_KEY
```

**Note:** The app works without API keys using public endpoints, but keys provide higher rate limits.

### Step 5: Run the App

. Connect an Android device via USB (with USB debugging enabled) OR start an Android emulator
. Click the **Run** button (green triangle) or press **Shift + F0**
. Select your device/emulator
4. Wait for the app to build and install

## Project Structure

```
SoothSayer/
 app/                          # Main application module
    build.gradle.kts         # App-level Gradle config
    proguard-rules.pro       # ProGuard configuration
    src/
        main/
           AndroidManifest.xml
           java/com/soothsayer/predictor/
              SoothSayerApplication.kt
              ui/          # UI components
                 MainActivity.kt
                 SplashActivity.kt
                 fragments/
                 adapters/
                 viewmodels/
              data/        # Data layer
                 models/
              utils/       # Utility classes
           res/             # Resources
               layout/      # XML layouts
               values/      # Strings, colors, themes
               drawable/    # Images and vector graphics
               navigation/  # Navigation graph
        test/                # Unit tests
        androidTest/         # Instrumentation tests
 gradle/                      # Gradle wrapper
 docs/                        # Documentation
    API_INTEGRATION.md
    ARCHITECTURE.md
    CONTRIBUTING.md
 build.gradle.kts            # Project-level Gradle config
 settings.gradle.kts         # Gradle settings
 gradle.properties           # Gradle properties
 .gitignore                  # Git ignore rules
 LICENSE                     # MIT License
 README.md                   # Main documentation
 CHANGELOG.md                # Version history
```

## Development Workflow

### Building the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### Code Quality

```bash
# Run lint checks
./gradlew lint

# Format code (if ktlint is configured)
./gradlew ktlintFormat
```

## Common Issues & Solutions

### Issue: Gradle Sync Failed

**Solution:**
. Check internet connection
. File → Invalidate Caches → Restart
. Delete `.gradle` folder and sync again

### Issue: SDK Not Found

**Solution:**
. Open **Tools** → **SDK Manager**
. Install Android SDK Platform 4 and Build Tools 4.0.0
. Sync Gradle again

### Issue: Emulator Won't Start

**Solution:**
. Open **Tools** → **AVD Manager**
. Create new virtual device with recommended settings
. Ensure hardware acceleration (Intel HAXM or AMD Hypervisor) is enabled

### Issue: App Crashes on Launch

**Solution:**
. Check Logcat for error messages
. Verify minimum SDK version (API 4+)
. Clean and rebuild: **Build** → **Clean Project** → **Rebuild Project**

## Development Tips

### Useful Android Studio Shortcuts

| Action | Windows/Linux | macOS |
|--------|---------------|-------|
| Build project | Ctrl + F9 | Cmd + F9 |
| Run app | Shift + F0 | Ctrl + R |
| Debug app | Shift + F9 | Ctrl + D |
| Find in project | Ctrl + Shift + F | Cmd + Shift + F |
| Reformat code | Ctrl + Alt + L | Cmd + Alt + L |
| Show Logcat | Alt + 6 | Cmd + 6 |

### Debugging

. **Enable Debug Logging**: Set `HttpLoggingInterceptor.Level.BODY` in network module
. **Use Logcat**: Filter by package name `com.soothsayer.predictor`
. **Database Inspector**: Tools → App Inspection → Database Inspector
4. **Network Inspector**: Tools → App Inspection → Network Inspector

### Performance Profiling

. **CPU Profiler**: Analyze method execution times
. **Memory Profiler**: Detect memory leaks
. **Network Profiler**: Monitor API calls and data usage

## Next Steps

### For Users
. Launch the app
. Select a cryptocurrency from the dropdown
. Configure pattern filters
4. Click "Analyze Patterns"
5. View detected patterns and predictions

### For Developers
. Read [ARCHITECTURE.md](docs/ARCHITECTURE.md) for system design
. Read [API_INTEGRATION.md](docs/API_INTEGRATION.md) for API details
. Read [CONTRIBUTING.md](docs/CONTRIBUTING.md) before making changes
4. Explore the codebase starting from `MainActivity.kt`

## Getting Help

### Resources
-  [Documentation](docs/)
-  [Issue Tracker](https://github.com/OWNER/soothsayer-predictor/issues)
-  [Discussions](https://github.com/OWNER/soothsayer-predictor/discussions)
-  Email: support@soothsayer.app

### Community
- Report bugs via GitHub Issues
- Request features via GitHub Discussions
- Contribute via Pull Requests

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file.

---

**Ready to predict crypto patterns? Let's go! **
