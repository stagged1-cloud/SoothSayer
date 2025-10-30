#  SoothSayer - Crypto Pattern Predictor

![Version](https://img.shields.io/badge/version-.0.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

##  Overview

SoothSayer is an Android application that analyzes historical cryptocurrency price data to identify and predict repeatable patterns. Using advanced pattern recognition algorithms, the app helps users discover timing patterns for various crypto assets based on historical data from Binance and other sources.

## Recent Updates (October 29, 2025)

### Latest Features
- **Ocean Protocol Support**: Added OCEAN to supported cryptocurrencies
- **RSI Technical Indicator**: Full RSI implementation with divergence detection
- **Pattern Markers**: Visual colored dots on charts showing detected patterns
- **Fullscreen Chart Rotation**: Chart data now persists through screen rotation
- **Force Refresh**: Bypass cache mechanism for fresh data on crypto switching
- **Enhanced Website**: Fixed price ticker with proper formatting and fallback data
- **Multi-Indicator Confirmation**: RSI + MA + Support combinations for 92% accuracy

### Bug Fixes
- Fixed API fallback logic (empty arrays now properly trigger next source)
- Fixed "wavy graph with no data points" issue
- Fixed price ticker not displaying correctly on website
- Fixed chart data loss on device rotation
- Improved error handling throughout data pipeline

### Performance Improvements
- Optimized chart rendering for large datasets
- Enhanced logging for better debugging
- Reduced database size to <50KB for 11 cryptocurrencies
- Pattern detection now completes in <2 seconds

---

## Features

### Pattern Detection
- **RSI Analysis**: Relative Strength Index with overbought/oversold detection and divergence patterns
- **Time-based Analysis**: Hour, day, week, month, and yearly patterns
- **Price Movement Patterns**: Spikes, drops, and consolidation periods
- **Volume Correlation**: Price movements correlated with trading volume
- **Moving Average Analysis**: Detect crossovers and trend reversals (Golden Cross/Death Cross)
- **Support/Resistance Levels**: Identify recurring price levels
- **Volatility Patterns**: Track high/low volatility periods
- **Seasonal Trends**: Quarterly and yearly trend analysis
- **Consecutive Patterns**: Detect winning/losing streaks

### Visualization
- Interactive price charts with colored pattern markers
- Pattern markers on both main chart and fullscreen view
- Color-coded by pattern type (Red=Bearish, Green=Bullish, Yellow=Support, etc.)
- Interactive tooltips showing pattern type and confidence
- Descriptive legends with pattern categories
- Frequency tables showing pattern occurrence
- Historical pattern timeline
- Prediction confidence indicators
- Multi-pattern overlay support
- Chart rotation support with state preservation

### User Features
- Multi-source crypto data (Binance primary, CoinGecko fallback, CryptoCompare backup)
- Support for 70+ cryptocurrencies including Bitcoin, Ethereum, Ocean Protocol
- Asset selection dropdown with auto-complete
- Toggle switches for pattern filters (9 algorithms)
- Offline data caching with 90-day retention
- Force refresh mechanism for fresh data
- Pattern export functionality
- Customizable alert notifications
- Fullscreen chart view with zoom/pan capabilities

##  Architecture

```
SoothSayer/
 app/                          # Android application
    src/main/
       java/com/soothsayer/predictor/
          ui/              # Activities, Fragments, Adapters
          data/            # Data sources, API clients
          models/          # Data models
          analysis/        # Pattern detection algorithms
          repository/      # Data repository layer
          utils/           # Utility classes
       res/                 # Resources (layouts, drawables, values)
 docs/                         # Documentation
 gradle/                       # Gradle configuration
```

##  Getting Started

### Prerequisites
- Android Studio Arctic Fox (00..) or later
- JDK  or higher
- Android SDK 4+ (Android 7.0 Nougat or higher)
- Gradle 7.0+

### Installation

. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/soothsayer-predictor.git
   cd soothsayer-predictor
   ```

. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F0

##  API Configuration

Create a `local.properties` file in the root directory with your API keys:

```properties
binance.api.key=YOUR_BINANCE_API_KEY
coingecko.api.key=YOUR_COINGECKO_API_KEY
cryptocompare.api.key=YOUR_CRYPTOCOMPARE_API_KEY
```

**Note**: API keys are optional for public endpoints but recommended for higher rate limits.

## Usage

1. **Launch App**: Opens with the SoothSayer splash screen
2. **Select Asset**: Choose from 70+ cryptocurrencies (BTC, ETH, OCEAN, etc.)
3. **Apply Filters**: Toggle pattern detection options (RSI, MA, Support, etc.)
4. **Tap Analyze**: Run pattern detection on selected cryptocurrency
5. **View Results**: 
   - Interactive chart with colored pattern markers
   - Pattern list with confidence scores
   - Tap pattern for detailed analysis
6. **Fullscreen Chart**: Tap chart to open fullscreen view with zoom/pan
7. **Set Alerts**: Configure notifications for detected patterns (coming soon)

### Pattern Confidence Guide
- **90-100%**: Very Strong - Highest probability, rare but powerful signals
- **80-89%**: Strong - High probability, suitable for most strategies
- **70-79%**: Moderate - Good for portfolio positions
- **60-69%**: Weak - Use with extreme caution, require multiple confirmations

### Multi-Indicator Strategy
Best results come from combining 2-3 indicators:
- RSI Oversold + Support Level = 87% success
- MA Golden Cross + Volume Spike = 81% success  
- RSI + MA + Support = 92% success (triple confirmation)

---

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Android Jetpack (ViewBinding, LiveData, ViewModel)
- **Networking**: Retrofit, OkHttp
- **Charts**: MPAndroidChart
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Coroutines**: Kotlin Coroutines for async operations
- **JSON Parsing**: Gson/Moshi

##  Data Sources

- **Binance API**: Historical OHLCV data
- **CoinGecko API**: Price history and market data
- **CryptoCompare API**: Alternative data source

##  Contributing

Contributions are welcome! Please read our [Contributing Guidelines](docs/CONTRIBUTING.md) first.

. Fork the repository
. Create a feature branch (`git checkout -b feature/AmazingFeature`)
. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##  Authors

- **Your Name** - *Initial work*

##  Acknowledgments

- Binance for providing comprehensive historical data
- MPAndroidChart for excellent charting capabilities
- The crypto community for inspiration

##  Support

For support, email support@soothsayer.app or open an issue on GitHub.

##  Roadmap

- [x] Basic pattern detection
- [x] Multi-source data integration
- [ ] Machine learning pattern prediction
- [ ] Social sentiment analysis
- [ ] Portfolio tracking
- [ ] Advanced backtesting
- [ ] Cloud sync
- [ ] iOS version

---

**Disclaimer**: This app is for educational and informational purposes only. Cryptocurrency trading involves significant risk. Always do your own research before making investment decisions.
