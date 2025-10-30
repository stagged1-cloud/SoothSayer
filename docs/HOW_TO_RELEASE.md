# How to Create a Signed Release APK for GitHub

## Current Status
✅ Release APK built successfully: `app-release-unsigned.apk`  
⚠️ APK needs to be signed before distribution

## Option 1: Quick Release (Debug Signed)

For initial testing/release, you can use the debug APK which is already signed:

### Steps:
1. Copy the debug APK:
   ```bash
   cp "app\build\outputs\apk\debug\app-debug.apk" "SoothSayer-v1.0.0-debug.apk"
   ```

2. Create a GitHub Release:
   - Go to https://github.com/stagged1-cloud/SoothSayer/releases
   - Click "Draft a new release"
   - Tag: `v1.0.0`
   - Title: `SoothSayer v1.0.0 - First Official Release`
   - Description: Copy content from `RELEASE_NOTES_v1.0.0.md`
   - Upload `SoothSayer-v1.0.0-debug.apk`
   - Check "This is a pre-release" (optional for v1.0.0)
   - Click "Publish release"

**Note**: Debug APKs work fine but use the default debug keystore. For production, use Option 2.

---

## Option 2: Production Release (Properly Signed)

For official releases with your own signature:

### Step 1: Generate a Signing Key (One-Time Setup)

Open PowerShell and run:

```powershell
# Navigate to your project directory
cd "C:\Users\don_t\Desktop\Projects\Soothsayer Predictor"

# Generate keystore (replace YOUR_NAME and YOUR_EMAIL)
keytool -genkey -v -keystore soothsayer-release-key.jks `
  -alias soothsayer `
  -keyalg RSA `
  -keysize 2048 `
  -validity 10000 `
  -storepass YourStrongPassword `
  -keypass YourStrongPassword `
  -dname "CN=YOUR_NAME, OU=Development, O=SoothSayer, L=Your City, ST=Your State, C=US"
```

**IMPORTANT**:
- Replace `YourStrongPassword` with a strong password
- Replace `YOUR_NAME`, `Your City`, etc. with your information
- **SAVE THE PASSWORD** - You'll need it for all future releases
- **BACKUP THE .jks FILE** - Store it securely (never commit to Git!)

### Step 2: Configure Gradle Signing

Create or edit `gradle.properties` (in project root):

```properties
# Add these lines (replace with your actual values)
RELEASE_STORE_FILE=soothsayer-release-key.jks
RELEASE_STORE_PASSWORD=YourStrongPassword
RELEASE_KEY_ALIAS=soothsayer
RELEASE_KEY_PASSWORD=YourStrongPassword
```

**IMPORTANT**: Add `gradle.properties` to `.gitignore` to keep passwords secret!

### Step 3: Update build.gradle.kts

Add signing configuration to `app/build.gradle.kts`:

```kotlin
android {
    // ... existing config ...
    
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("gradle.properties")
            val keystoreProperties = java.util.Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(keystorePropertiesFile.inputStream())
                
                storeFile = rootProject.file(keystoreProperties["RELEASE_STORE_FILE"] as String)
                storePassword = keystoreProperties["RELEASE_STORE_PASSWORD"] as String
                keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"] as String
                keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"] as String
            }
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")  // Add this line
        }
    }
}
```

### Step 4: Build Signed Release APK

```bash
.\gradlew assembleRelease
```

The signed APK will be at: `app\build\outputs\apk\release\app-release.apk`

### Step 5: Rename and Verify

```powershell
# Rename APK
cp "app\build\outputs\apk\release\app-release.apk" "SoothSayer-v1.0.0.apk"

# Verify signature (optional)
keytool -printcert -jarfile "SoothSayer-v1.0.0.apk"
```

### Step 6: Create GitHub Release

1. Go to https://github.com/stagged1-cloud/SoothSayer/releases
2. Click "Draft a new release"
3. Fill in:
   - **Tag**: `v1.0.0`
   - **Title**: `SoothSayer v1.0.0 - First Official Release`
   - **Description**: Paste from `RELEASE_NOTES_v1.0.0.md`
4. Attach files:
   - `SoothSayer-v1.0.0.apk` (signed release)
   - (Optional) `RELEASE_NOTES_v1.0.0.md` as documentation
5. Click "Publish release"

---

## Option 3: GitHub Actions Automation (Advanced)

For automated releases on every tag push, create `.github/workflows/release.yml`:

```yaml
name: Android Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        
      - name: Build Release APK
        run: ./gradlew assembleRelease
        env:
          RELEASE_STORE_PASSWORD: ${{ secrets.RELEASE_STORE_PASSWORD }}
          RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
          
      - name: Create Release
        uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/apk/release/*.apk
          body_path: RELEASE_NOTES_v1.0.0.md
```

Then add secrets to GitHub:
- Go to Settings → Secrets → Actions
- Add `RELEASE_STORE_PASSWORD` and `RELEASE_KEY_PASSWORD`

---

## Recommended: Start with Option 1

For your first release, I recommend **Option 1** (debug signed):
- ✅ Works immediately
- ✅ No keystore setup needed
- ✅ Perfect for testing and early releases
- ✅ Can be installed by users right away

**Later**, switch to Option 2 for official releases.

---

## After Publishing

### Update Website
Edit `docs/index.html` to point to the new release:

```html
<!-- Download Section -->
<a href="https://github.com/stagged1-cloud/SoothSayer/releases/download/v1.0.0/SoothSayer-v1.0.0.apk" 
   class="btn btn-primary" target="_blank">Download APK</a>
```

### Announce the Release
- Share on social media
- Post in crypto/Android communities
- Update README.md with download badge
- Create a blog post or video demo

### Monitor Issues
- Watch GitHub issues for bug reports
- Respond to user feedback
- Plan next version based on requests

---

## Checklist for v1.0.0 Release

- [x] Build release APK (unsigned exists)
- [x] Create RELEASE_NOTES_v1.0.0.md
- [ ] **Choose Option 1 or 2** (Start with debug for quick release)
- [ ] Copy/rename APK to `SoothSayer-v1.0.0.apk`
- [ ] Create GitHub Release with tag `v1.0.0`
- [ ] Upload APK to release
- [ ] Copy release notes to GitHub release description
- [ ] Update website download link
- [ ] Test APK installation on device
- [ ] Announce release
- [ ] Add .gitignore entries for keystore files

---

## Security Best Practices

### Never Commit These Files:
```
# Add to .gitignore
*.jks
*.keystore
gradle.properties  # If it contains passwords
local.properties
```

### Backup Strategy:
1. Store keystore in password manager (1Password, LastPass, etc.)
2. Keep encrypted backup on USB drive
3. Store password separately from keystore
4. Document key details (alias, validity, creation date)

### If You Lose the Keystore:
- ❌ Cannot update existing app installations
- ❌ Must publish new app with different package name
- ❌ Users must uninstall old version
- ✅ Always backup immediately after creation!

---

## Quick Command Reference

```powershell
# Build debug (already signed)
.\gradlew assembleDebug

# Build release (unsigned)
.\gradlew assembleRelease

# Build release (signed - after setup)
.\gradlew assembleRelease

# Install debug on connected device
.\gradlew installDebug

# Clean build
.\gradlew clean

# List all tasks
.\gradlew tasks
```

---

## Ready to Release?

**For immediate release**, run these commands:

```powershell
# Navigate to project
cd "C:\Users\don_t\Desktop\Projects\Soothsayer Predictor"

# Copy debug APK (already signed and working)
Copy-Item "app\build\outputs\apk\debug\app-debug.apk" -Destination "SoothSayer-v1.0.0.apk"

# Verify file exists
Get-Item "SoothSayer-v1.0.0.apk"
```

Then:
1. Go to https://github.com/stagged1-cloud/SoothSayer/releases/new
2. Tag: `v1.0.0`
3. Title: `SoothSayer v1.0.0 - First Official Release`
4. Upload `SoothSayer-v1.0.0.apk`
5. Paste release notes from `RELEASE_NOTES_v1.0.0.md`
6. Click **Publish release**

That's it! Your app will be available for download! 🎉
