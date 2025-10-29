# Pattern Detection Algorithms - Technical Documentation

## Overview
SoothSayer uses **statistical pattern recognition** rather than traditional chart patterns (Head & Shoulders, Triangles, etc.). The system analyzes historical price data to find **repeatable behaviors** based on time, volume, volatility, and price levels.

---

## Current Algorithms (8 Types)

### 1. Time-Based Pattern Detection
**Types:** Hourly, Daily, Weekly, Monthly, Yearly

**What It Looks For:**
- Specific times (hour/day/week/month/quarter) that consistently show similar price movements
- Example: "Bitcoin tends to rise on Mondays" or "Ethereum drops between 2-3 PM"

**How It Works:**
```
1. Group all price data by time unit (e.g., all Mondays, all hour 14:00)
2. Calculate average return (% change) for each group
3. Calculate consistency using standard deviation
4. If average return > threshold AND consistency > 60%, pattern detected
```

**Calculation:**
- **Average Return** = Mean of `((close - open) / open) * 100` for all occurrences
- **Consistency** = `1.0 - (stdDev / 10.0)` (lower deviation = higher consistency)
- **Confidence** = Consistency score (0.0 - 1.0)

**Thresholds:**
- Hourly: >0.5% avg return, >0.6 consistency, ≥10 occurrences
- Daily: >0.8% avg return, >0.65 consistency, ≥8 occurrences  
- Weekly: >1.5% avg return, >0.6 consistency
- Monthly: >2.0% avg return, >0.6 consistency, ≥5 occurrences
- Yearly/Quarterly: >3.0% avg return, >0.55 consistency, ≥3 occurrences

**Prediction:**
- Predicts next occurrence based on calendar (e.g., next Monday at 2 PM)

---

### 2. Moving Average Crossovers
**What It Looks For:**
- When short-term moving average (7-day) crosses long-term moving average (30-day)
- Similar to the "Golden Cross" (bullish) and "Death Cross" (bearish) indicators

**How It Works:**
```
1. Calculate 7-day MA and 30-day MA for all price data
2. Find points where 7-day crosses above (bullish) or below (bearish) 30-day
3. Measure average price change 7 days after each crossover
4. If crossovers occurred ≥3 times, pattern detected
```

**Calculation:**
- **Moving Average** = Average of closing prices over N days
- **Crossover** = Sign change of (MA7 - MA30)
- **Average Return After Signal** = Mean price change 7 days after crossover
- **Confidence** = `min(0.9, crossovers / 10)` (more crossovers = higher confidence)

**Thresholds:**
- Requires ≥50 days of data
- Minimum 3 crossovers detected

**Prediction:**
- No prediction (reactive pattern)

---

### 3. Volume Correlation Patterns
**What It Looks For:**
- Relationship between high trading volume and price movement
- Example: "High volume spikes correlate with 2% price increases"

**How It Works:**
```
1. Calculate average volume across all data
2. Filter for "high volume" days (>2x average volume)
3. Measure price change on those high-volume days
4. If pattern is consistent, detected
```

**Calculation:**
- **High Volume Threshold** = `Average Volume * 2`
- **Price Change** = `((close - open) / open) * 100` on high-volume days
- **Consistency** = `1.0 - (avg deviation from mean / 10.0)`

**Thresholds:**
- ≥5 high-volume spikes
- >1.0% average price change
- >0.6 consistency

**Prediction:**
- No prediction (correlation indicator)

---

### 4. Volatility Analysis
**What It Looks For:**
- Periods of high volatility (large price swings) or low volatility (consolidation)
- High volatility often precedes breakouts; low volatility often precedes major moves

**How It Works:**
```
1. Calculate daily volatility: (high - low) / low * 100
2. Calculate average volatility
3. Find periods where volatility > 1.5x average (high) or < 0.5x average (low)
4. Track occurrences and following price action
```

**Calculation:**
- **Daily Volatility** = `((high - low) / low) * 100`
- **High Volatility** = Volatility > `avg * 1.5`
- **Low Volatility** = Volatility < `avg * 0.5`

**Thresholds:**
- ≥5 occurrences of high or low volatility
- Confidence fixed at 0.7

**Prediction:**
- No specific prediction (context indicator)

---

### 5. Support & Resistance Levels
**What It Looks For:**
- Price levels that are repeatedly "tested" (price bounces off them)
- Support = price floor (lows cluster around a level)
- Resistance = price ceiling (highs cluster around a level)

**How It Works:**
```
1. Extract all daily lows (for support) and highs (for resistance)
2. Group similar price levels within 2% tolerance
3. Count how many times each level was tested
4. If level tested ≥3 times, pattern detected
```

**Calculation:**
- **Level Clustering** = Prices within 2% of each other grouped together
- **Test Count** = Number of times price touched the level
- **Confidence** = `min(0.95, tests / 8)` (more tests = stronger level)

**Thresholds:**
- ≥3 tests of the same level
- 2% price tolerance for grouping

**Prediction:**
- No prediction (price level indicator)

**Example:**
- "Support level at $42,150 tested 13 times" (92% confidence)

---

### 6. Seasonal Trends
**What It Looks For:**
- Quarterly patterns (Q1, Q2, Q3, Q4) showing consistent trends
- Example: "Q4 historically shows 5.2% average gain"

**How It Works:**
```
1. Group all data by quarter (Q1 = Jan-Mar, Q2 = Apr-Jun, etc.)
2. Calculate average return for each quarter across multiple years
3. Calculate consistency
4. If avg return > 5% and consistency > 0.6, pattern detected
```

**Calculation:**
- Same as time-based patterns but grouped by quarter
- **Quarter** = `(month - 1) / 3 + 1`

**Thresholds:**
- >5.0% average return
- >0.6 consistency
- ≥3 occurrences

**Prediction:**
- Predicts next occurrence of that quarter

---

### 7. Consecutive Patterns (Streaks)
**What It Looks For:**
- Streaks of consecutive winning days (gains) or losing days (drops)
- Example: "Consecutive winning streak pattern: up to 7 days in a row, occurred 37 times"

**How It Works:**
```
1. Iterate through sorted price data day by day
2. Track consecutive gains (close > open) and losses (close < open)
3. Count streaks of 3+ consecutive days
4. Track maximum streak length and total occurrences
```

**Calculation:**
- **Gain** = `(close - open) / open > 0`
- **Streak** = 3+ consecutive days of same direction
- **Confidence** = `min(0.85, streaks / 10)`

**Thresholds:**
- ≥3 occurrences of 3+ day streaks

**Prediction:**
- No prediction (trend continuation indicator)

---

### 8. Price Movement Patterns (Spikes/Drops)
**What It Looks For:**
- Sudden large price movements (spikes up or drops down)
- Identifies "outlier" days with unusually large moves

**How It Works:**
```
1. Calculate all daily price changes
2. Calculate average absolute price change
3. Find days where change > 2x average (spikes or drops)
4. Count occurrences
```

**Calculation:**
- **Price Change** = `((close - open) / open) * 100`
- **Spike** = Change > `avg * 2`
- **Drop** = Change < `-avg * 2`

**Thresholds:**
- ≥5 occurrences
- Confidence fixed at 0.75

**Prediction:**
- No prediction (volatility indicator)

---

## Key Metrics Explained

### Confidence Score (0% - 100%)
- **Formula varies by pattern type**, but generally:
  - Based on consistency of historical occurrences
  - Higher when more data points align with the pattern
  - Capped at realistic maximums (e.g., 95% for support/resistance)

### Frequency
- **Number of times the pattern occurred** in historical data
- Higher frequency = more reliable pattern
- Example: Pattern seen 37 times vs 3 times

### Average Return
- **Expected price change** when pattern occurs
- Calculated as: `((close - open) / open) * 100`
- Positive = bullish, Negative = bearish

### Consistency
- **How reliable the pattern is** (inverse of standard deviation)
- `1.0 - (stdDev / 10.0)`
- Higher = more predictable outcomes
- Lower = more variation in results

---

## What SoothSayer Does NOT Do (Yet)

### Traditional Chart Patterns NOT Implemented:
- ❌ Head & Shoulders / Inverse H&S
- ❌ Cup & Handle
- ❌ Triangles (Ascending, Descending, Symmetrical)
- ❌ Flags & Pennants
- ❌ Double Top / Double Bottom
- ❌ Wedges
- ❌ Channels

### Technical Indicators NOT Implemented:
- ❌ RSI (Relative Strength Index)
- ❌ MACD (Moving Average Convergence Divergence)
- ❌ Bollinger Bands
- ❌ Fibonacci Retracements
- ❌ Ichimoku Cloud
- ❌ Stochastic Oscillator

### Advanced Features NOT Implemented:
- ❌ Multi-timeframe analysis
- ❌ Correlation with BTC/market
- ❌ Order book analysis
- ❌ News sentiment integration
- ❌ Machine learning predictions

---

## Filtering System

Users can filter patterns by:
- **Minimum Confidence:** Only show patterns with X% or higher confidence
- **Minimum Frequency:** Only show patterns that occurred X or more times
- **Pattern Types:** Enable/disable specific algorithm types

---

## Data Requirements

- **Minimum Data:** 90 days of historical price data (OHLCV)
- **API Source:** Binance (primary), CoinGecko (fallback), CryptoCompare (backup)
- **Update Frequency:** On-demand (when user selects a cryptocurrency)
- **Storage:** Last 90 days stored locally in Room database

---

## Limitations & Considerations

### Current Limitations:
1. **Historical bias:** Patterns based solely on past behavior, no fundamental analysis
2. **No volume in all patterns:** Some patterns don't utilize volume data fully
3. **Fixed thresholds:** Thresholds are hardcoded, not adaptive per crypto
4. **No breakout detection:** Doesn't identify when patterns invalidate
5. **No multi-crypto correlation:** Each crypto analyzed independently

### Statistical Disclaimers:
- **Past performance ≠ future results**
- Crypto markets are highly volatile and unpredictable
- Patterns can break at any time
- Use as one tool among many, not sole decision factor
- Always use risk management and stop losses

---

## Potential Improvements

### Short-term (Easy):
1. Add RSI indicator (overbought/oversold)
2. Add MACD crossovers
3. Add Bollinger Band squeeze detection
4. Improve volume analysis across all pattern types
5. Make thresholds configurable per user

### Medium-term (Moderate):
6. Implement Head & Shoulders detection
7. Implement Triangle pattern detection
8. Add multi-timeframe analysis (1h, 4h, 1d, 1w)
9. Add pattern invalidation alerts
10. Correlation analysis with Bitcoin

### Long-term (Complex):
11. Machine learning for pattern prediction
12. News sentiment analysis integration
13. Order book depth analysis
14. Real-time WebSocket price updates
15. Portfolio-wide pattern scanning

---

## Technical Details

### Programming Language:
- Kotlin (Android-native)

### Key Files:
- `PatternAnalyzer.kt` - Core detection engine (585 lines)
- `DetectPatternsUseCase.kt` - Orchestration layer
- `CryptoRepository.kt` - Data fetching
- `PatternDao.kt` - Local storage

### Dependencies:
- Kotlin Coroutines - Async processing
- Hilt - Dependency injection
- Room - Local database
- Retrofit - API calls

---

*Last Updated: October 29, 2025*
