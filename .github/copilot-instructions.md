# SoothSayer - Crypto Pattern Predictor

## Project Overview
**SoothSayer** is an Android app that detects and predicts repeatable patterns in cryptocurrency price data using advanced pattern recognition algorithms.

## Language & Framework
- **Primary Language**: Kotlin (for speed, reliability, and Android-first support)
- **Architecture**: Clean Architecture + MVVM
- **UI**: Jetpack Compose / XML layouts with ViewBinding
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp

## Architecture

### Layer Structure
```
UI Layer (Fragments/ViewModels)
    ↓
Domain Layer (Use Cases)
    ↓
Data Layer (Repository + APIs + Room DB)
```

### Key Components
- **PatternAnalyzer**: Implements 8+ pattern detection algorithms
- **CryptoRepository**: Multi-source data fetching (Binance → CoinGecko → CryptoCompare)
- **DetectPatternsUseCase**: Coordinates data fetching and pattern analysis
- **Room Database**: Optimized local storage (keeps ~50KB for 0 cryptos, 90 days)

## Data Sources (Priority Order)
. **Binance API** (PRIMARY) - Free, fast, reliable, no API key needed
. **CoinGecko API** (FALLBACK) - Free tier, broad coverage
. **CryptoCompare API** (BACKUP) - Alternative data source

## Pattern Detection Types (ALL IMPLEMENTED)
. Time-based (hourly, daily, weekly, monthly, yearly)
. Moving averages (7-day/0-day crossovers)
. Volume correlation (price-volume relationships)
4. Volatility analysis (high/low volatility periods)
5. Support/Resistance levels
6. Seasonal trends (quarterly patterns)
7. Consecutive patterns (winning/losing streaks)
8. Price movements (spikes, drops, consolidation)

## Storage Strategy
- **Local (Room)**: Last 90 days + all patterns (~4KB total)
- **Cloud**: Optional future enhancement
- **Auto-cleanup**: Deletes data older than 90 days to keep app lightweight

## Development Workflows

### Setup
```bash
# Open in Android Studio
# Sync Gradle (automatic)
# Optional: Add API keys to local.properties
```

### Build & Run
```bash
./gradlew assembleDebug        # Debug build
./gradlew assembleRelease      # Release build
./gradlew installDebug         # Install on device
```

### Testing
```bash
./gradlew test                 # Unit tests
./gradlew connectedAndroidTest # Instrumentation tests
./gradlew lint                 # Lint checks
```

## Coding Conventions

### Kotlin Style
- Follow official Kotlin coding conventions
- Use 4 spaces for indentation
- Max line length: 0 characters
- Meaningful variable names (no single letters except i, j in loops)
- KDoc comments for public APIs

### File Organization
```
com.soothsayer.predictor/
 ui/              # Activities, Fragments, ViewModels, Adapters
 domain/          # Use cases
 data/            # Repository, API clients, DAOs
 models/          # Data classes
 analysis/        # Pattern detection algorithms
 di/              # Hilt modules
 utils/           # Utility classes
```

### Naming Conventions
- Activities: `MainActivity`, `SplashActivity`
- Fragments: `AnalysisFragment`
- ViewModels: `AnalysisViewModel`
- Use Cases: `DetectPatternsUseCase`
- DAOs: `PriceDataDao`
- Adapters: `PatternAdapter`

### Error Handling
```kotlin
sealed class Resource<T> {
    class Success<T>(data: T) : Resource<T>()
    class Error<T>(message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```

## Key Files & Directories

### Core Files
- `app/src/main/java/com/soothsayer/predictor/`
  - `analysis/PatternAnalyzer.kt` - Main pattern detection engine
  - `data/repository/CryptoRepository.kt` - Multi-source data access
  - `domain/usecases/DetectPatternsUseCase.kt` - Pattern detection orchestration
  - `di/AppModule.kt` - Dependency injection configuration

### Configuration
- `app/build.gradle.kts` - Dependencies and build configuration
- `gradle.properties` - Gradle settings
- `local.properties` - API keys (not committed)

### Documentation
- `README.md` - Main documentation
- `docs/ARCHITECTURE.md` - Architecture details
- `docs/API_INTEGRATION.md` - API integration guide
- `docs/PROJECT_SUMMARY.md` - Technology decisions

## Dependencies & Integration Points

### Network
- Binance API: `https://api.binance.com`
- CoinGecko API: `https://api.coingecko.com/api/v/`
- CryptoCompare API: `https://min-api.cryptocompare.com`

### Libraries
- Hilt .48 - DI
- Room .6.0 - Database
- Retrofit .9.0 - HTTP client
- Coroutines .7. - Async
- MPAndroidChart ..0 - Charts

## Performance Targets
- Database size: < 50KB for 0 cryptos
- API response cache: 4 hours
- Pattern detection: <  seconds for 65 days of data
- App size: < 0 MB

## Notes for AI Agents
- Use Kotlin coroutines for all async operations
- Always use dependency injection (Hilt)
- Follow Clean Architecture principles
- Optimize for minimal database size
- Implement multi-source fallback for reliability
- Add KDoc comments for public APIs
- Use sealed classes for state management
- Prefer immutable data classes
- Test all pattern detection algorithms thoroughly
- **NEVER use emojis in code, comments, documentation, or any output**
