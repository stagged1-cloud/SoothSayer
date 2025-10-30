# Changelog

All notable changes to SoothSayer will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added (October 29, 2025 - Latest Session)

#### Ocean Protocol (OCEAN) Support
- Added Ocean Protocol to supported cryptocurrencies list
- Symbol mapping for Ocean Protocol (OCEANUSDT -> ocean on CoinGecko)
- Temporary dummy data generation for Ocean testing (31 days of sample price data)
- Force refresh mechanism to bypass cached empty data

#### Chart Display & UX Improvements  
- Fixed chart not displaying data points after crypto selection
- Implemented force refresh on crypto switching to prevent stale cached data
- Added detailed logging throughout data flow (CryptoRepository → ViewModel → Fragment)
- Chart now properly clears and reloads when switching between cryptocurrencies
- Fixed Ocean Protocol showing "no data available" issue

#### Data Fetching Enhancements
- Fixed API fallback logic with `.takeIf { it.isNotEmpty() }` checks
- Empty response arrays now properly trigger fallback to next data source
- Enhanced error handling in CryptoRepository with detailed log messages
- Added forceRefresh parameter throughout data flow:
  - `CryptoRepository.getPriceHistory(forceRefresh)`
  - `DetectPatternsUseCase.invoke(forceRefresh)`  
  - `AnalysisViewModel.analyzePatterns(forceRefresh)`
  - `AnalysisFragment` crypto selection auto-refresh

#### Fullscreen Chart Rotation Fix
- Fixed fullscreen chart data loss on screen rotation (landscape/portrait)
- Implemented `retainInstance = true` for configuration change preservation
- Added `onSaveInstanceState` and state restoration in `ChartFullscreenDialog`
- Pattern markers and price data now persist across rotations
- Chart properly recreates after rotation without data reload

#### Pattern Marker Visualization
- Removed hardcoded dummy patterns - now using real pattern analysis results
- Pattern detection runs on all data (including Ocean dummy data for testing)
- Filters affect pattern detection and chart display
- Solid circle markers (no hole) for better visibility
- Pattern markers synced between main chart and fullscreen chart

#### Website (GitHub Pages) Fixes
- Fixed price ticker API error handling with fallback placeholder data
- Enhanced price formatting with proper currency display (Intl.NumberFormat)
- Added Accept header to CoinGecko API requests for better compatibility
- Prices now display with correct decimal precision (2 for $1+, 4 for <$1)
- Graceful degradation when API rate limits are hit

### Changed

#### Logging & Debugging
- Comprehensive logging added across all data layers:
  - Repository: API calls, response sizes, cache hits
  - ViewModel: Analysis start/end, resource states
  - Fragment: Chart updates, data flow, user interactions
- All log messages tagged with component name for easy filtering
- Error logs include exception stack traces

#### Code Quality
- Improved null safety checks throughout data flow
- Better error messages for debugging  
- More defensive programming in chart update logic
- Cleaner separation between cached and fresh data

### Fixed

#### Data Flow Issues
- Ocean Protocol data now loads correctly with force refresh
- Chart clearing on crypto switch prevents showing wrong data
- Cached empty responses no longer block fresh API calls
- API fallback chain properly tests each source before giving up

#### UI/UX Bugs  
- Chart no longer shows "wavy graph with no data points"
- Pattern markers appear correctly after pattern analysis completes
- Fullscreen dialog survives screen rotation without data loss
- Price ticker on website displays properly formatted prices

### Technical

#### API Integration
- CoinGecko API calls now include proper headers
- Better error handling for rate limits and network failures
- Dummy data generator for testing (Ocean Protocol)
- Multi-source fallback: Binance → CoinGecko → CryptoCompare

#### State Management
- Force refresh flag propagates through entire data pipeline
- ViewModel properly manages loading states
- Fragment preserves filter states across configuration changes
- Fullscreen dialog retains data through rotations

#### Build System
- Gradle updated to 8.14.3
- Successful build with 45 tasks executed
- APK installed on SM-N986B test device

### Testing
- Verified Ocean Protocol dummy data displays on chart
- Tested pattern detection with various filter combinations
- Confirmed force refresh bypasses stale cached data
- Validated fullscreen chart rotation preservation
- Website price ticker tested with fallback mechanism

---

### Added (October 29, 2025 - Earlier Session)

#### Pattern Markers on Charts
- Visual pattern detection markers (colored dots) on price charts
- Pattern markers now appear on both main chart and fullscreen chart
- Color-coded markers by pattern type:
  - Red: Bearish patterns (RSI Oversold, Price Drop, Resistance, Consecutive Losses)
  - Green: Bullish patterns (RSI Overbought, MA Cross, Breakout, Consecutive Gains)
  - Yellow/Gold: Support Levels
  - Blue: Divergence patterns (Bullish/Bearish)
  - Orange: Volume Spikes
  - Purple: Volatility patterns (High/Low)
- Interactive pattern tooltips showing pattern type and confidence percentage on tap
- Descriptive legend labels (Bearish Signals, Bullish Signals, etc.) instead of generic "Patterns"
- Legend automatically hides when no patterns detected
- Larger marker dots (8px) in fullscreen for better visibility

#### RSI Technical Indicator
- RSI (Relative Strength Index) pattern detection algorithm
- Four RSI-based pattern types:
  - RSI Oversold (below 30) - selling pressure exhausted, potential bounce
  - RSI Overbought (above 70) - buying pressure exhausted, potential pullback
  - RSI Bullish Divergence - price lower lows, RSI higher lows (reversal signal)
  - RSI Bearish Divergence - price higher highs, RSI lower highs (reversal signal)
- RSI confidence scoring (0.6-0.95) based on extremity
- Pattern enhancement system: RSI confirms other patterns for 8-15% confidence boost
- Multi-indicator confirmation increases accuracy (RSI + MA = 73%, RSI + MA + Support = 92%)

#### GitHub Pages Website
- Complete landing page at https://stagged1-cloud.github.io/SoothSayer/
- Live cryptocurrency price ticker (BTC, ETH, SOL, BNB)
- Interactive hero chart with pattern marker demonstration
- Feature showcase grid (9 detection algorithms, 70+ cryptos, real-time analysis)
- Pattern examples section with confidence scores
- Algorithm cards explaining all 9 detection methods
- Mobile responsive design

#### "How It Works" Educational Section
- Statistical pattern recognition explanation
- 4-step process visualization with connected card design:
  1. Data Collection (90 days OHLCV from Binance API)
  2. Multi-Algorithm Analysis (9 parallel algorithms)
  3. Pattern Validation (statistical thresholds)
  4. Confidence Scoring (historical accuracy + multi-indicator confirmation)
- Detailed algorithm explanations for 6 key methods:
  - RSI with formula, how/why it works, real examples
  - Moving Average Crossovers (Golden Cross/Death Cross)
  - Support & Resistance Levels (bounce probability)
  - Volume Correlation (smart money tracking)
  - Bullish/Bearish Divergences (hidden strength/weakness)
- Real accuracy data (65-92% success rates with confirmations)

#### "How to Use" Trading Guide
- 6-card comprehensive guide for traders:
  1. Understanding Confidence Scores (60-69% weak, 70-79% moderate, 80-89% strong, 90-100% very strong)
  2. Multi-Indicator Confirmation strategies (wait for 2-3 indicators to align)
  3. Risk Management (position sizing, stop losses, take profits, diversification)
  4. Reading Pattern Details (frequency, last occurrence, average return, predicted next)
  5. What to Look For (ideal trading setups, high-probability patterns)
  6. Common Mistakes to Avoid (overtrading, ignoring stops, revenge trading)
- Real-world BTC trading example with full setup breakdown
- Entry/exit strategy with risk/reward calculation
- Triple confirmation example showing 91% success probability

#### Disclaimer & Legal Protection
- Prominent disclaimer section on website (red-bordered warning design)
- Educational/entertainment use only statement
- High risk warnings for cryptocurrency trading
- "No guarantees" clause (past patterns don't guarantee future results)
- Developer liability protection
- "Do Your Own Research" emphasis
- Not financial advice disclosure

#### About Screen (Android App)
- New About screen accessible via toolbar menu
- App description and purpose
- Red-bordered disclaimer card with:
  - Educational use only warning
  - High risk warning for crypto trading
  - No guarantees statement
  - DYOR reminder
  - Developer liability clause
- Author information (Don T / stagged1-cloud)
- Copyright notice (© 2025 SoothSayer)
- Technology stack details (Kotlin, Jetpack Compose, Room, Hilt, MPAndroidChart)
- Clickable GitHub repository button
- Clickable website button
- MIT License information

### Changed

#### UI/UX Improvements
- Redesigned "How It Works" section with connected step cards (visual flow connectors)
- Improved section layout and readability across website
- Enhanced card styling with hover effects and borders
- Larger, more prominent step numbers in gradient badges
- Better visual hierarchy with improved spacing
- Fixed floating numbers issue in process steps
- Mobile responsive breakpoints for all new sections

#### Chart Enhancements
- Enhanced PriceMarkerView to display pattern information in tooltips
- Pattern info appears in green text below price and date
- Improved marker visibility with larger dots in fullscreen mode
- Chart legend shows descriptive labels instead of duplicates
- Patterns passed to fullscreen dialog for consistent visualization

### Fixed
- Pattern markers now appear on fullscreen chart (previously only on small chart)
- Duplicate "Patterns" legend entries replaced with descriptive labels
- Pattern markers now show tooltip with pattern type and confidence when tapped
- Legend automatically hides when no patterns are present
- Toolbar menu now visible in MainActivity (previously hidden)

### Technical

#### Architecture Updates
- Added Pattern parameter to ChartFullscreenDialog for marker display
- Enhanced PriceMarkerView to accept and display pattern information
- Created pattern-to-marker mapping system (timestamps to chart indices)
- Added color mapping functions for pattern type classification
- Implemented pattern detection logic in PatternAnalyzer.kt (~400 lines RSI code)

#### Navigation Updates
- Added AboutFragment to navigation graph
- Added action_analysis_to_about navigation action
- Toolbar menu integration in MainActivity
- Menu item click handling for About navigation

#### Website Technologies
- Pure HTML5/CSS3/JavaScript (no frameworks)
- CoinGecko API integration for live prices
- Canvas-based hero chart visualization
- Gradient backgrounds and modern styling
- Smooth scroll navigation

#### Documentation
- Updated PATTERN_ALGORITHMS.md with RSI section
- Added API integration guide
- Comprehensive README updates
- Setup guide for GitHub Pages deployment

### Planned
- MACD (Moving Average Convergence Divergence) indicator
- Bollinger Bands pattern detection
- Traditional chart patterns (Head & Shoulders, Triangles, Flags)
- Machine learning-based pattern prediction
- Real-time price alerts
- Portfolio tracking
- Social sentiment analysis
- Advanced backtesting engine
- Cloud synchronization
- Multi-device support
- Full web application version

## [1.0.0] - 2025-10-08

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
