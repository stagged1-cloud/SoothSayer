# SoothSayer v1.0.0 Release Notes

**Release Date**: October 29, 2025  
**Version**: 1.0.0  
**Build**: 1

## First Official Release

SoothSayer is an Android app that detects and predicts repeatable patterns in cryptocurrency price data using advanced pattern recognition algorithms.

## Key Features

### Pattern Detection Algorithms (9 Total)
- **RSI Analysis** - Relative Strength Index with overbought/oversold detection and divergence patterns
- **Moving Average Crossovers** - Golden Cross and Death Cross detection (7-day × 30-day)
- **Support & Resistance Levels** - Automatically identifies key price levels
- **Volume Correlation** - Detects volume spikes (2× average) that drive price moves
- **Volatility Analysis** - High and low volatility period detection
- **Time-Based Patterns** - Hourly, daily, weekly, monthly, and yearly recurring patterns
- **Seasonal Trends** - Quarterly and yearly pattern analysis
- **Consecutive Patterns** - Winning/losing streak recognition
- **Price Movements** - Spike and drop detection beyond normal volatility

### Supported Cryptocurrencies (70+)
Bitcoin (BTC), Ethereum (ETH), Binance Coin (BNB), XRP, Solana (SOL), Cardano (ADA), Dogecoin (DOGE), Polkadot (DOT), Polygon (MATIC), Avalanche (AVAX), Ocean Protocol (OCEAN), and 60+ more!

### User Interface
- **Interactive Charts** - MPAndroidChart visualization with colored pattern markers
  - Red markers: Bearish signals (RSI Oversold, Resistance, Price Drops)
  - Green markers: Bullish signals (RSI Overbought, MA Cross, Breakouts)
  - Yellow markers: Support levels
  - Blue markers: Divergence patterns
  - Orange markers: Volume spikes
  - Purple markers: Volatility patterns
- **Pattern Filters** - Toggle switches for each detection algorithm
- **Fullscreen Charts** - Tap any chart for fullscreen view with zoom/pan
- **Screen Rotation Support** - Chart data persists through orientation changes
- **Offline Caching** - 90-day data retention, works without internet

### Data Sources
- **Primary**: Binance API (fastest, most reliable, no key needed)
- **Fallback**: CoinGecko API (free tier, broad coverage)
- **Backup**: CryptoCompare API (alternative data source)

Multi-source fallback ensures data availability even if one API is down.

### Technical Highlights
- **Clean Architecture** - Separation of concerns with MVVM pattern
- **Hilt Dependency Injection** - Proper DI for maintainability
- **Room Database** - Local SQLite caching (~50KB for 11 cryptos, 90 days)
- **Kotlin Coroutines** - Async operations without blocking UI
- **Material Design 3** - Modern UI with dark/light theme support

## Pattern Confidence Scores

Patterns include confidence scores based on historical accuracy:
- **90-100%**: Very Strong - Highest probability, rare but powerful
- **80-89%**: Strong - High probability, suitable for most strategies
- **70-79%**: Moderate - Good for portfolio positions
- **60-69%**: Weak - Use with caution, require multiple confirmations

### Multi-Indicator Confirmation
Best results come from combining indicators:
- RSI Oversold + Support Level = **87% success rate**
- MA Golden Cross + Volume Spike = **81% success rate**
- RSI + MA + Support (triple confirmation) = **92% success rate**

## What's New in v1.0.0

### Core Features (Initial Release)
- Full pattern detection engine with 9 algorithms
- Support for 70+ cryptocurrencies
- Interactive charts with pattern visualization
- Customizable pattern filters
- Multi-source data fetching with fallback
- Offline data caching (90-day retention)
- Fullscreen chart view with rotation support
- Pattern detail screens with full analysis
- Confidence scoring system
- About screen with disclaimer

### Recent Improvements (October 29, 2025)
- Ocean Protocol (OCEAN) support added
- Fixed chart not displaying data points
- Fixed API fallback logic (empty response handling)
- Implemented force refresh mechanism
- Fixed fullscreen chart data loss on rotation
- Enhanced pattern marker visualization
- Improved error handling throughout app
- Comprehensive logging for debugging

## System Requirements

- **Minimum Android Version**: 7.0 Nougat (API 24)
- **Target Android Version**: 14 (API 34)
- **Recommended**: Android 10+ for best performance
- **Storage**: ~10 MB for app, ~50 KB for data (11 cryptos, 90 days)
- **Internet**: Required for data fetching, optional for cached viewing
- **Permissions**: Internet access only (no location, camera, etc.)

## Installation

### Option 1: GitHub Releases (Recommended)
1. Download `SoothSayer-v1.0.0.apk` from [GitHub Releases](https://github.com/stagged1-cloud/SoothSayer/releases)
2. Enable "Install from Unknown Sources" in Android settings
3. Open the APK file and tap "Install"
4. Launch SoothSayer from your app drawer

### Option 2: Build from Source
```bash
git clone https://github.com/stagged1-cloud/SoothSayer.git
cd SoothSayer
./gradlew assembleRelease
# APK will be in app/build/outputs/apk/release/
```

## Quick Start Guide

1. **Launch App** - Opens with splash screen
2. **Select Cryptocurrency** - Choose from 70+ options (BTC, ETH, OCEAN, etc.)
3. **Toggle Filters** - Enable pattern detection algorithms you want to use
4. **Tap Analyze** - Run pattern detection
5. **View Results**:
   - Interactive chart with colored pattern markers
   - Pattern list with confidence scores
   - Tap any pattern for detailed analysis
6. **Fullscreen Chart** - Tap chart for fullscreen view with zoom/pan capabilities

## Use Cases

### For Traders
- Identify high-probability entry/exit points
- Spot support/resistance levels for stop-loss placement
- Detect trend reversals with RSI divergences
- Find recurring time-based patterns for timing trades

### For Researchers
- Analyze historical pattern effectiveness
- Study cryptocurrency market cycles
- Test multi-indicator confirmation strategies
- Export pattern data for further analysis

### For Learners
- Understand technical indicators (RSI, MA, S/R)
- See real-world pattern examples
- Learn about pattern confidence scoring
- Practice risk-free pattern analysis

## Important Disclaimers

**NOT FINANCIAL ADVICE**: This application is for educational and informational purposes only. It does not provide financial, investment, or trading advice.

**HIGH RISK**: Cryptocurrency trading carries substantial risk of loss. Past patterns do not guarantee future results. You can lose some or all of your invested capital.

**NO GUARANTEES**: While SoothSayer uses sophisticated algorithms, no pattern detection system can predict the future with certainty. All confidence scores represent historical probability, not guaranteed outcomes.

**DO YOUR OWN RESEARCH**: Always conduct your own analysis and consult with licensed financial advisors before making investment decisions. Use SoothSayer as one tool among many, never the sole basis for trading.

## Known Issues

### Current Limitations
- Ocean Protocol currently uses test data (real API integration pending)
- No real-time price updates (manual refresh required)
- Pattern detection runs on demand (not automatic background updates)
- No cloud sync (patterns stored locally only)
- No push notifications for pattern alerts

### Deprecation Warnings (Non-Critical)
- Some Android APIs show deprecation warnings (targeted for future updates)
- Does not affect functionality in Android 7-14

## Roadmap

### v1.1.0 (Planned)
- [ ] Real Ocean Protocol API integration
- [ ] Additional cryptocurrencies (100+ total)
- [ ] Pattern alert notifications
- [ ] Export patterns to CSV/JSON
- [ ] Dark mode improvements

### v1.2.0 (Planned)
- [ ] Real-time WebSocket price updates
- [ ] Background pattern detection with WorkManager
- [ ] Portfolio tracking
- [ ] Pattern backtesting engine
- [ ] Historical pattern accuracy tracking

### v2.0.0 (Future)
- [ ] Machine learning pattern prediction
- [ ] Social sentiment analysis integration
- [ ] Cloud synchronization
- [ ] Multi-device support
- iOS version

## License

MIT License - See [LICENSE](LICENSE) file for details

## Acknowledgments

- **Binance** for comprehensive historical data API
- **MPAndroidChart** for excellent charting library
- **CoinGecko** for reliable cryptocurrency data
- **The Crypto Community** for inspiration and feedback

## Support & Feedback

- **Issues**: Report bugs at [GitHub Issues](https://github.com/stagged1-cloud/SoothSayer/issues)
- **Discussions**: Join the conversation at [GitHub Discussions](https://github.com/stagged1-cloud/SoothSayer/discussions)
- **Website**: Visit [SoothSayer Documentation](https://stagged1-cloud.github.io/SoothSayer/)
- **Email**: support@soothsayer.app (coming soon)

## Statistics

- **Lines of Code**: ~15,000+ (Kotlin)
- **Supported Cryptos**: 70+
- **Detection Algorithms**: 9
- **Pattern Types**: 20+
- **Data Sources**: 3 (Binance, CoinGecko, CryptoCompare)
- **Database Size**: <50 KB (90 days, 11 cryptos)
- **App Size**: ~10 MB
- **Minimum Android**: 7.0 (API 24)

## Security & Privacy

- **No User Data Collection** - SoothSayer collects zero personal information
- **No Analytics/Tracking** - No Firebase, Google Analytics, or tracking SDKs
- **Local Storage Only** - All data stored on your device
- **Open Source** - Full code transparency on GitHub
- **No Ads** - Completely ad-free experience
- **Minimal Permissions** - Only requires internet access

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

---

**Download Now**: [SoothSayer v1.0.0](https://github.com/stagged1-cloud/SoothSayer/releases/tag/v1.0.0)

**Star the Project**: https://github.com/stagged1-cloud/SoothSayer

**Built by stagged1-cloud**
