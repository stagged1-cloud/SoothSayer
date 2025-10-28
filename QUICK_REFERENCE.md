#  SoothSayer - Quick Reference Card

##  QUICK START ( Steps)

### ⃣ Open in Android Studio
```
File → Open → Navigate to project folder → OK
Wait for Gradle sync (- minutes)
```

### ⃣ Run the App
```
Connect Android device OR start emulator
Click  Run button (or Shift+F0)
App launches with splash screen!
```

### ⃣ Push to GitHub
```powershell
# Run the initialization script:
.\init-git.ps

# Or manually:
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/soothsayer-predictor.git
git push -u origin main
```

---

##  KEY FILES YOU SHOULD KNOW

| File | What It Does | When to Edit |
|------|--------------|--------------|
| `PatternAnalyzer.kt` | ALL pattern detection logic | Add new pattern types |
| `CryptoRepository.kt` | Fetches data from APIs | Change data sources |
| `AnalysisViewModel.kt` | Manages UI state | Add new UI features |
| `AppModule.kt` | Dependency injection | Add new dependencies |
| `build.gradle.kts` | Dependencies & config | Add libraries |
| `strings.xml` | All text in the app | Change UI text |

---

##  PATTERN DETECTION FILTERS

**All Implemented & Working:**

 **Hourly** - Specific hours show trends  
 **Daily** - Day of week patterns  
 **Weekly** - Week number patterns  
 **Monthly** - Month-based trends  
 **Yearly** - Seasonal/quarterly  
 **Moving Averages** - MA crossovers  
 **Volume** - Price-volume correlation  
 **Volatility** - High/low vol periods  
 **Support/Resistance** - Price levels  
 **Seasonal** - Quarterly trends  

**Default Enabled:** Daily, Weekly, Monthly, MA, Volume, Support/Resistance

---

##  DATA SOURCES (Auto Fallback)

```
Try : Binance  (PRIMARY)
   Fails? → Try : CoinGecko 
       Fails? → Try : CryptoCompare 
           Fails? → Use Cache 
```

**Binance (Best):**
- Speed: ~00ms
- Free: Yes
- API Key: Not needed
- Limit: 00/min

---

##  STORAGE INFO

**Local Database Size:**
- 0 cryptos × 90 days = ~4 KB
- Auto-cleanup keeps it small
- Offline mode always works

**What's Stored:**
-  Last 90 days price data
-  All detected patterns
-  User preferences
-  No personal data
-  No API keys in DB

---

##  ARCHITECTURE LAYERS

```
UI (Fragments/Activities)
  ↓ ViewModels
  ↓
Domain (Use Cases)
  ↓
Data (Repository → APIs + Database)
```

**Key Classes:**
- `DetectPatternsUseCase` - Main pattern detection
- `CryptoRepository` - Data management
- `AnalysisViewModel` - UI state
- `PatternAnalyzer` - Algorithm engine

---

##  COMMON TASKS

### Add New Crypto
**File:** `CryptoRepository.kt`
```kotlin
private val SUPPORTED_SYMBOLS = listOf(
    "BTCUSDT" to "Bitcoin",
    "NEWCOIN" to "New Coin" // Add here
)
```

### Change Cache Duration
**File:** `CryptoRepository.kt`
```kotlin
private const val CACHE_VALIDITY_HOURS = 4 // Change here
```

### Adjust Pattern Sensitivity
**File:** `AnalysisViewModel.kt`
```kotlin
minimumConfidence = 0.6,  // Lower = more patterns
minimumFrequency =       // Lower = detect rare patterns
```

### Add New Pattern Type
**Files:** 
. Add to `PatternType` enum in `Models.kt`
. Implement in `PatternAnalyzer.kt`
. Add filter in `AnalysisFragment.kt`

---

##  TESTING

```bash
# Unit tests
./gradlew test

# Android instrumentation tests
./gradlew connectedAndroidTest

# Lint checks
./gradlew lint

# Build APK
./gradlew assembleDebug
```

**Test Files Location:**
- Unit: `app/src/test/`
- Integration: `app/src/androidTest/`

---

##  SUPPORTED DEVICES

**Minimum:** Android 7.0 (API 4)  
**Target:** Android 4 (API 4)  
**Recommended:** Android 0+ for best experience

---

##  TROUBLESHOOTING

### Gradle Sync Failed
```
File → Invalidate Caches → Restart
Delete .gradle folder → Sync again
```

### App Crashes on Launch
```
Check Logcat (Alt+6)
Verify minimum SDK is 4
Clean & Rebuild project
```

### No Data Loading
```
Check internet connection
Verify API URLs in BuildConfig
Check Logcat for API errors
Try clearing app data
```

### Database Error
```
Uninstall app
Reinstall from Android Studio
Database recreates automatically
```

---

##  DOCUMENTATION FILES

| Doc | What's Inside |
|-----|---------------|
| `README.md` | Overview & features |
| `ARCHITECTURE.md` | System design |
| `API_INTEGRATION.md` | API details |
| `PROJECT_SUMMARY.md` | Tech decisions |
| `SETUP.md` | Dev environment |
| `GITHUB_SYNC.md` | Git workflow |
| `CONTRIBUTING.md` | How to contribute |

---

##  PERFORMANCE TARGETS

 App Size: < 0 MB (currently ~8 MB)  
 DB Size: < 50 KB (currently ~4 KB)  
 API Response: < 500ms (Binance ~00ms)  
 Pattern Detection: < s (currently -s)  
 Offline: Must work ( full cache)

---

##  SECURITY CHECKLIST

 HTTPS only  
 No hardcoded keys  
 ProGuard enabled  
 API keys in local.properties  
 No logs in production  
 Database encryption ready  

---

##  DEPLOYMENT

### Debug Build (Testing)
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build (Production)
```bash
# Create keystore first:
keytool -genkey -v -keystore soothsayer.keystore -alias soothsayer -keyalg RSA -keysize 048 -validity 0000

# Build signed APK:
./gradlew assembleRelease
```

---

##  PRO TIPS

. **Use Live Templates** - Create snippets for common patterns
. **Enable Auto-Import** - Settings → Editor → Auto Import
. **Use Logcat Filters** - Filter by "soothsayer" tag
4. **Profile Performance** - Tools → Profiler
5. **Test on Real Device** - More accurate than emulator

---

##  USEFUL LINKS

- **Android Docs:** https://developer.android.com
- **Kotlin Docs:** https://kotlinlang.org/docs
- **Hilt Guide:** https://dagger.dev/hilt
- **Room Guide:** https://developer.android.com/training/data-storage/room
- **Binance API:** https://binance-docs.github.io/apidocs

---

##  NEED HELP?

. Check `docs/` folder
. Search GitHub issues
. Read inline code comments
4. Check Android Studio hints
5. Review Logcat errors

---

##  QUICK WINS

**Easy Features to Add First:**
. More cryptocurrencies (just add to list)
. Custom time ranges (change days parameter)
. Export patterns to CSV
4. Share pattern screenshots
5. Custom confidence thresholds

---

** You're all set! Happy coding!**

*Print this card and keep it handy while developing!*
