# SoothSayer - TODO List

## Completed Features
- [x] Searchable cryptocurrency selector with 70+ coins
- [x] Real-time data status display (shows "Xm ago", "Xh ago", etc.)
- [x] Added Hyperliquid (HYPE) to crypto list
- [x] Fixed crypto selection persistence when navigating back
- [x] Fixed chart tooltip freezing during drag
- [x] Enabled keyboard input for cryptocurrency search
- [x] Fixed dropdown showing all options after navigation
- [x] Auto-analyze patterns on crypto selection
- [x] Fixed chart not updating when switching cryptos
- [x] Removed Monero (XMR) - not available on Binance
- [x] Improved time display ("Today" instead of "0 days ago")
- [x] Comprehensive pattern detection algorithm documentation
- [x] Remove all emojis from codebase and documentation

---

## Pending Features

### High Priority
- [ ] Real-time prices in pattern detail section
  - Show current price at top of detail view
  - Display price change since pattern last occurred
  - Add live price updates (optional WebSocket)

### Pattern Detection Improvements

#### Phase 1: Technical Indicators (Easy - 1-2 days each)
- [ ] Add RSI (Relative Strength Index)
  - Detect overbought (>70) and oversold (<30) conditions
  - Pattern: "RSI below 30 preceded 15 bounces with 78% success"
  
- [ ] Add MACD (Moving Average Convergence Divergence)
  - Detect bullish/bearish crossovers
  - Measure average return after crossover signals
  
- [ ] Add Bollinger Bands
  - Detect "squeeze" (bands narrowing)
  - Detect price touching upper/lower bands
  - Pattern: "BB squeeze preceded 5.8% moves in 12/15 cases"
  
- [ ] Add Volume-Weighted Average Price (VWAP)
  - Identify price above/below VWAP
  - Institutional trading levels

#### Phase 2: Classic Chart Patterns (Medium - 1-2 weeks each)
- [ ] Enhance Support/Resistance Detection
  - Improve clustering algorithm
  - Add breakout/breakdown detection
  - Show strength of level (number of tests)
  
- [ ] Implement Head & Shoulders Detection
  - Identify 3 peaks with specific formation
  - Verify neckline
  - Predict target price after break
  
- [ ] Implement Inverse Head & Shoulders
  - Mirror of H&S for bullish reversals
  - Common in crypto bottoms
  
- [ ] Add Triangle Pattern Detection
  - Ascending triangles (bullish)
  - Descending triangles (bearish)
  - Symmetrical triangles (breakout either way)
  - Calculate convergence point and target
  
- [ ] Add Flag & Pennant Patterns
  - Detect sharp move (flagpole)
  - Identify consolidation (flag/pennant body)
  - Predict trend continuation
  
- [ ] Add Double Top/Bottom Detection
  - Currently have support/resistance - enhance to detect specific 2-peak formations
  - More precise than current clustering
  
- [ ] Add Cup & Handle Pattern
  - U-shaped cup formation
  - Small downward handle
  - Bullish continuation pattern

#### Phase 3: Advanced Features (Complex - 2-4 weeks each)
- [ ] Multi-Timeframe Analysis
  - Scan 15m, 1h, 4h, 1d, 1w simultaneously
  - Pattern must align across timeframes
  - Higher confidence when all agree
  
- [ ] Bitcoin Correlation Analysis
  - Measure altcoin correlation to BTC
  - Weight patterns when BTC shows same pattern
  - Identify decoupling opportunities
  
- [ ] Trend Reversal Detection
  - Identify when uptrend is ending
  - Accumulation/distribution patterns
  - Volume divergence
  
- [ ] Breakout Direction Prediction
  - Currently detects consolidation
  - Add: Predict which direction breakout will occur
  - Use volume, momentum, previous trend
  
- [ ] Pattern Invalidation Alerts
  - Detect when pattern breaks/fails
  - Stop-loss recommendations
  - Pattern failure statistics
  
- [ ] Machine Learning Predictions
  - Train on historical pattern success rates
  - Predict which patterns will succeed
  - Adapt to changing market conditions
  - Requires: TensorFlow Lite integration

---

### UI/UX Improvements
- [ ] Add educational tooltips for each pattern type
- [ ] Show visual examples of patterns in detail view
- [ ] Add pattern strength indicator (weak/moderate/strong)
- [ ] Implement pattern favoriting/bookmarking
- [ ] Add chart annotations showing where patterns occurred
- [ ] Dark mode improvements (enhance contrast)
- [ ] Add settings screen for:
  - Configurable confidence thresholds
  - Minimum frequency filters
  - Notification preferences
  
- [ ] Pattern performance tracking
  - Track if predictions came true
  - Show success rate over time
  - Learn from failures

---

### Data & Performance
- [ ] Real-time price updates via WebSocket
  - Binance WebSocket integration
  - Live chart updates
  - Live pattern detection
  
- [ ] Multi-cryptocurrency pattern scanning
  - Scan all 70+ cryptos at once
  - Show "hottest" patterns across market
  - Portfolio-wide analysis
  
- [ ] Cloud sync (optional)
  - Save favorite cryptos to cloud
  - Sync settings across devices
  - Pattern alerts via push notifications
  
- [ ] Improve data caching
  - Reduce API calls
  - Faster load times
  - Offline mode support
  
- [ ] Optimize database
  - Better indexing
  - Query optimization
  - Background cleanup

---

### Testing & Quality
- [ ] Add unit tests for PatternAnalyzer
- [ ] Add integration tests for API calls
- [ ] Add UI tests for critical flows
- [ ] Performance benchmarking
- [ ] Memory leak detection
- [ ] Crash reporting integration (Firebase Crashlytics)

---

### Documentation
- [ ] Add in-app help/tutorial
- [ ] Create user guide
- [ ] Add video tutorials
- [ ] Document API integration for contributors
- [ ] Add contributing guidelines
- [ ] Create architecture diagrams

---

### Nice to Have
- [ ] Export patterns to CSV/PDF
- [ ] Share patterns on social media
- [ ] Price alerts based on patterns
- [ ] Compare patterns across multiple cryptos
- [ ] Historical backtesting tool
- [ ] Paper trading integration
- [ ] News sentiment analysis
- [ ] Order book depth analysis
- [ ] Whale movement tracking
- [ ] Fear & Greed index integration

---

## Technical Debt
- [ ] Fix deprecation warnings
  - `getParcelableArrayList` in AnalysisFragment
  - Update to Android 14+ APIs
  
- [ ] Remove unused variable `fillColor` in AnalysisFragment
- [ ] Improve error handling in ViewModel
- [ ] Add ProGuard rules for release build
- [ ] Optimize image assets
- [ ] Add content descriptions for accessibility
- [ ] Implement proper logging framework
- [ ] Replace hardcoded strings with resources

---

## Release Checklist
- [ ] Complete beta testing
- [ ] Create app icon (multiple sizes)
- [ ] Create promotional screenshots
- [ ] Write app store description
- [ ] Prepare privacy policy
- [ ] Set up Google Play Console
- [ ] Configure release signing
- [ ] Enable crash reporting
- [ ] Enable analytics
- [ ] Create changelog
- [ ] Submit to Google Play Store

---

## Current Status
**Version:** 1.0-beta  
**Last Updated:** October 29, 2025  
**Total Cryptocurrencies:** 70  
**Pattern Algorithms:** 8 statistical + 0 traditional  
**GitHub:** https://github.com/stagged1-cloud/SoothSayer

---

## Notes
- Prioritize indicators (RSI, MACD, Bollinger) before complex patterns
- All pattern improvements should include:
  - Algorithm documentation
  - Example explanations
  - Confidence scoring
  - Backtesting validation
- Never use emojis in code, comments, or documentation
- Follow Clean Architecture principles
- Maintain <50KB database size goal
