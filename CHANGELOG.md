# Changelog

All notable changes to SoothSayer will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v.0.0.html).

## [Unreleased]

### Planned
- Machine learning-based pattern prediction
- Real-time price alerts
- Portfolio tracking
- Social sentiment analysis
- Advanced backtesting engine
- Cloud synchronization
- Multi-device support

## [.0.0] - 05-0-8

### Added
- Initial release of SoothSayer
- Cryptocurrency price data fetching from Binance API
- Historical data storage with Room database
- Pattern detection engine with multiple algorithms:
  - Time-based pattern detection (hourly, daily, weekly, monthly, yearly)
  - Price movement patterns (spikes, drops, consolidation)
  - Technical indicators (moving averages, support/resistance)
  - Volume correlation analysis
  - Volatility pattern detection
  - Seasonal trend analysis
- Interactive price charts using MPAndroidChart
- Customizable pattern filters via toggle switches
- Pattern frequency and prediction display
- Splash screen with custom branding
- Clean Architecture with MVVM pattern
- Offline data caching
- Material Design  UI

### Features
- Support for 0+ major cryptocurrencies (BTC, ETH, BNB, etc.)
- Configurable confidence thresholds
- Pattern history tracking
- Real-time data updates
- Responsive UI with dark/light theme support

### Technical
- Kotlin programming language
- Jetpack components (Room, LiveData, ViewModel, Navigation)
- Retrofit for API communication
- Hilt for dependency injection
- Coroutines for async operations
- MPAndroidChart for visualization

### Documentation
- Comprehensive README with setup instructions
- API integration guide
- Architecture documentation
- Contributing guidelines

## Version History

### Version Numbering
- **Major** (.x.x): Breaking changes, major new features
- **Minor** (x..x): New features, backward compatible
- **Patch** (x.x.): Bug fixes, minor improvements

---

[Unreleased]: https://github.com/OWNER/soothsayer-predictor/compare/v.0.0...HEAD
[.0.0]: https://github.com/OWNER/soothsayer-predictor/releases/tag/v.0.0
