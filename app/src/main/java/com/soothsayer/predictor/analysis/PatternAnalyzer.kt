package com.soothsayer.predictor.analysis

import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PatternFilter
import com.soothsayer.predictor.data.models.PatternType
import com.soothsayer.predictor.data.models.PriceData
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Comprehensive pattern detection engine
 * Implements all 8+ pattern detection algorithms
 */
class PatternAnalyzer @Inject constructor() {
    
    /**
     * Main analysis entry point
     */
    fun analyze(
        priceData: List<PriceData>,
        filters: PatternFilter
    ): List<Pattern> {
        if (priceData.isEmpty()) return emptyList()
        
        val patterns = mutableListOf<Pattern>()
        
        // 1. Time-based patterns
        if (filters.enableHourlyAnalysis) {
            patterns.addAll(detectHourlyPatterns(priceData))
        }
        if (filters.enableDailyAnalysis) {
            patterns.addAll(detectDailyPatterns(priceData))
        }
        if (filters.enableWeeklyAnalysis) {
            patterns.addAll(detectWeeklyPatterns(priceData))
        }
        if (filters.enableMonthlyAnalysis) {
            patterns.addAll(detectMonthlyPatterns(priceData))
        }
        if (filters.enableYearlyAnalysis) {
            patterns.addAll(detectYearlyPatterns(priceData))
        }
        
        // 2. Moving averages
        if (filters.enableMovingAverages) {
            patterns.addAll(detectMovingAverageCrossPatterns(priceData))
        }
        
        // 3. Volume correlation
        if (filters.enableVolumeCorrelation) {
            patterns.addAll(detectVolumeCorrelationPatterns(priceData))
        }
        
        // 4. Volatility analysis
        if (filters.enableVolatilityAnalysis) {
            patterns.addAll(detectVolatilityPatterns(priceData))
        }
        
        // 5. Support/Resistance
        if (filters.enableSupportResistance) {
            patterns.addAll(detectSupportResistancePatterns(priceData))
        }
        
        // 6. Seasonal trends
        if (filters.enableSeasonalTrends) {
            patterns.addAll(detectSeasonalPatterns(priceData))
        }
        
        // 7. Consecutive patterns
        patterns.addAll(detectConsecutivePatterns(priceData))
        
        // 8. Price spike/drop patterns
        patterns.addAll(detectPriceMovementPatterns(priceData))
        
        // 9. RSI patterns (momentum oscillator)
        if (filters.enableRSI) {
            patterns.addAll(detectRSIPatterns(priceData))
        }
        
        // Enhance patterns with RSI confirmation
        if (filters.enableRSI) {
            enhancePatternsWithRSI(patterns, priceData)
        }
        
        // Filter by confidence and frequency
        return patterns.filter {
            it.confidence >= filters.minimumConfidence &&
            it.frequency >= filters.minimumFrequency
        }
    }
    
    /**
     * Detect hourly patterns (specific hours show consistent price movements)
     */
    private fun detectHourlyPatterns(data: List<PriceData>): List<Pattern> {
        val hourlyGroups = data.groupBy { getHourOfDay(it.timestamp) }
        val patterns = mutableListOf<Pattern>()
        
        hourlyGroups.forEach { (hour, prices) ->
            if (prices.size < 10) return@forEach
            
            val avgReturn = calculateAverageReturn(prices)
            val consistency = calculateConsistency(prices)
            
            if (abs(avgReturn) > 0.5 && consistency > 0.6) {
                val patternType = if (avgReturn > 0) PatternType.HOURLY_SPIKE else PatternType.HOURLY_DROP
                
                patterns.add(Pattern(
                    symbol = prices.first().symbol,
                    patternType = patternType,
                    confidence = consistency,
                    frequency = prices.size,
                    lastOccurrence = prices.maxOf { it.timestamp },
                    predictedNextOccurrence = predictNextOccurrence(hour, "hourly"),
                    description = "Price tends to ${if (avgReturn > 0) "rise" else "fall"} at hour $hour with ${String.format("%.1f", avgReturn)}% average change",
                    averageReturnPercentage = avgReturn
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect daily patterns (day of week analysis)
     */
    private fun detectDailyPatterns(data: List<PriceData>): List<Pattern> {
        val dayGroups = data.groupBy { getDayOfWeek(it.timestamp) }
        val patterns = mutableListOf<Pattern>()
        val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        
        dayGroups.forEach { (day, prices) ->
            if (prices.size < 8) return@forEach
            
            val avgReturn = calculateAverageReturn(prices)
            val consistency = calculateConsistency(prices)
            
            if (abs(avgReturn) > 0.8 && consistency > 0.65) {
                patterns.add(Pattern(
                    symbol = prices.first().symbol,
                    patternType = PatternType.DAILY_PATTERN,
                    confidence = consistency,
                    frequency = prices.size,
                    lastOccurrence = prices.maxOf { it.timestamp },
                    predictedNextOccurrence = predictNextOccurrence(day, "daily"),
                    description = "Price shows ${if (avgReturn > 0) "positive" else "negative"} trend on ${dayNames.getOrNull(day - 1)} (${String.format("%.1f", avgReturn)}% avg)",
                    averageReturnPercentage = avgReturn
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect weekly patterns
     */
    private fun detectWeeklyPatterns(data: List<PriceData>): List<Pattern> {
        val weekGroups = data.groupBy { getWeekOfYear(it.timestamp) }
        val patterns = mutableListOf<Pattern>()
        
        weekGroups.forEach { (week, prices) ->
            if (prices.isEmpty()) return@forEach
            
            val avgReturn = calculateAverageReturn(prices)
            val consistency = calculateConsistency(prices)
            
            if (abs(avgReturn) > 1.5 && consistency > 0.6) {
                patterns.add(Pattern(
                    symbol = prices.first().symbol,
                    patternType = PatternType.WEEKLY_PATTERN,
                    confidence = consistency,
                    frequency = prices.size,
                    lastOccurrence = prices.maxOf { it.timestamp },
                    predictedNextOccurrence = predictNextOccurrence(week, "weekly"),
                    description = "Week $week shows recurring ${if (avgReturn > 0) "bullish" else "bearish"} pattern (${String.format("%.1f", avgReturn)}% avg)",
                    averageReturnPercentage = avgReturn
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect monthly patterns
     */
    private fun detectMonthlyPatterns(data: List<PriceData>): List<Pattern> {
        val monthGroups = data.groupBy { getMonthOfYear(it.timestamp) }
        val patterns = mutableListOf<Pattern>()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        
        monthGroups.forEach { (month, prices) ->
            if (prices.size < 5) return@forEach
            
            val avgReturn = calculateAverageReturn(prices)
            val consistency = calculateConsistency(prices)
            
            if (abs(avgReturn) > 2.0 && consistency > 0.6) {
                patterns.add(Pattern(
                    symbol = prices.first().symbol,
                    patternType = PatternType.MONTHLY_PATTERN,
                    confidence = consistency,
                    frequency = prices.size,
                    lastOccurrence = prices.maxOf { it.timestamp },
                    predictedNextOccurrence = predictNextOccurrence(month, "monthly"),
                    description = "${monthNames.getOrNull(month - 1)} historically shows ${String.format("%.1f", avgReturn)}% average change",
                    averageReturnPercentage = avgReturn
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect yearly/seasonal patterns
     */
    private fun detectYearlyPatterns(data: List<PriceData>): List<Pattern> {
        val quarterGroups = data.groupBy { getQuarterOfYear(it.timestamp) }
        val patterns = mutableListOf<Pattern>()
        
        quarterGroups.forEach { (quarter, prices) ->
            if (prices.size < 3) return@forEach
            
            val avgReturn = calculateAverageReturn(prices)
            val consistency = calculateConsistency(prices)
            
            if (abs(avgReturn) > 3.0 && consistency > 0.55) {
                patterns.add(Pattern(
                    symbol = prices.first().symbol,
                    patternType = PatternType.YEARLY_PATTERN,
                    confidence = consistency,
                    frequency = prices.size,
                    lastOccurrence = prices.maxOf { it.timestamp },
                    predictedNextOccurrence = predictNextOccurrence(quarter, "quarterly"),
                    description = "Q$quarter shows recurring ${if (avgReturn > 0) "growth" else "decline"} pattern (${String.format("%.1f", avgReturn)}% avg)",
                    averageReturnPercentage = avgReturn
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect moving average crossover patterns
     */
    private fun detectMovingAverageCrossPatterns(data: List<PriceData>): List<Pattern> {
        if (data.size < 50) return emptyList()
        
        val patterns = mutableListOf<Pattern>()
        val sortedData = data.sortedBy { it.timestamp }
        
        // 7-day and 30-day MA crossover
        val ma7 = calculateMovingAverage(sortedData, 7)
        val ma30 = calculateMovingAverage(sortedData, 30)
        
        val crossovers = findCrossovers(ma7, ma30)
        
        if (crossovers.size >= 3) {
            val avgReturnAfterCross = calculateAverageReturnAfterSignal(sortedData, crossovers, 7)
            
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.MOVING_AVERAGE_CROSS,
                confidence = minOf(0.9, crossovers.size / 10.0),
                frequency = crossovers.size,
                lastOccurrence = crossovers.lastOrNull()?.first ?: 0,
                predictedNextOccurrence = null,
                description = "7-day MA crosses 30-day MA with ${String.format("%.1f", avgReturnAfterCross)}% avg return in next week",
                averageReturnPercentage = avgReturnAfterCross
            ))
        }
        
        return patterns
    }
    
    /**
     * Detect volume-price correlation patterns
     */
    private fun detectVolumeCorrelationPatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        val highVolumeSpikes = data.filter { 
            it.volume > data.map { d -> d.volume }.average() * 2
        }
        
        if (highVolumeSpikes.size >= 5) {
            val priceChanges = highVolumeSpikes.map {
                ((it.close - it.open) / it.open) * 100
            }
            
            val avgPriceChange = priceChanges.average()
            val consistency = 1.0 - (priceChanges.map { abs(it - avgPriceChange) }.average() / 10.0)
            
            if (abs(avgPriceChange) > 1.0 && consistency > 0.6) {
                patterns.add(Pattern(
                    symbol = data.first().symbol,
                    patternType = PatternType.VOLUME_SPIKE,
                    confidence = consistency.coerceIn(0.0, 1.0),
                    frequency = highVolumeSpikes.size,
                    lastOccurrence = highVolumeSpikes.maxOf { it.timestamp },
                    predictedNextOccurrence = null,
                    description = "High volume spikes correlate with ${String.format("%.1f", avgPriceChange)}% price ${if (avgPriceChange > 0) "increase" else "decrease"}",
                    averageReturnPercentage = avgPriceChange
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect volatility patterns
     */
    private fun detectVolatilityPatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        val volatilities = data.map { (it.high - it.low) / it.low * 100 }
        val avgVolatility = volatilities.average()
        
        // High volatility periods
        val highVolPeriods = data.zip(volatilities).filter { it.second > avgVolatility * 1.5 }
        if (highVolPeriods.size >= 5) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.HIGH_VOLATILITY,
                confidence = 0.7,
                frequency = highVolPeriods.size,
                lastOccurrence = highVolPeriods.maxOf { it.first.timestamp },
                predictedNextOccurrence = null,
                description = "High volatility periods (${String.format("%.1f", avgVolatility * 1.5)}%+ daily range) occur ${highVolPeriods.size} times",
                averageReturnPercentage = highVolPeriods.map { ((it.first.close - it.first.open) / it.first.open) * 100 }.average()
            ))
        }
        
        // Low volatility periods
        val lowVolPeriods = data.zip(volatilities).filter { it.second < avgVolatility * 0.5 }
        if (lowVolPeriods.size >= 5) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.LOW_VOLATILITY,
                confidence = 0.7,
                frequency = lowVolPeriods.size,
                lastOccurrence = lowVolPeriods.maxOf { it.first.timestamp },
                predictedNextOccurrence = null,
                description = "Low volatility consolidation periods detected ${lowVolPeriods.size} times, often precede breakouts",
                averageReturnPercentage = 0.0
            ))
        }
        
        return patterns
    }
    
    /**
     * Detect support and resistance levels
     */
    private fun detectSupportResistancePatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        
        // Find recurring price levels
        val supportLevels = findRecurringLevels(data.map { it.low }, 0.02)
        val resistanceLevels = findRecurringLevels(data.map { it.high }, 0.02)
        
        supportLevels.forEach { (level, occurrences) ->
            if (occurrences.size >= 3) {
                patterns.add(Pattern(
                    symbol = data.first().symbol,
                    patternType = PatternType.SUPPORT_LEVEL,
                    confidence = minOf(0.95, occurrences.size / 8.0),
                    frequency = occurrences.size,
                    lastOccurrence = data[occurrences.last()].timestamp,
                    predictedNextOccurrence = null,
                    description = "Support level at $${String.format("%.2f", level)} tested ${occurrences.size} times",
                    averageReturnPercentage = 0.0
                ))
            }
        }
        
        resistanceLevels.forEach { (level, occurrences) ->
            if (occurrences.size >= 3) {
                patterns.add(Pattern(
                    symbol = data.first().symbol,
                    patternType = PatternType.RESISTANCE_LEVEL,
                    confidence = minOf(0.95, occurrences.size / 8.0),
                    frequency = occurrences.size,
                    lastOccurrence = data[occurrences.last()].timestamp,
                    predictedNextOccurrence = null,
                    description = "Resistance level at $${String.format("%.2f", level)} tested ${occurrences.size} times",
                    averageReturnPercentage = 0.0
                ))
            }
        }
        
        return patterns
    }
    
    /**
     * Detect seasonal/quarterly trends
     */
    private fun detectSeasonalPatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        val quarterlyData = data.groupBy { getQuarterOfYear(it.timestamp) }
        
        quarterlyData.forEach { (quarter, prices) ->
            if (prices.size >= 3) {
                val avgReturn = calculateAverageReturn(prices)
                val consistency = calculateConsistency(prices)
                
                if (abs(avgReturn) > 5.0 && consistency > 0.6) {
                    patterns.add(Pattern(
                        symbol = prices.first().symbol,
                        patternType = PatternType.SEASONAL_TREND,
                        confidence = consistency,
                        frequency = prices.size,
                        lastOccurrence = prices.maxOf { it.timestamp },
                        predictedNextOccurrence = predictNextOccurrence(quarter, "quarterly"),
                        description = "Q$quarter seasonal trend: ${String.format("%.1f", avgReturn)}% average ${if (avgReturn > 0) "gain" else "loss"}",
                        averageReturnPercentage = avgReturn
                    ))
                }
            }
        }
        
        return patterns
    }
    
    /**
     * Detect consecutive gain/loss patterns
     */
    private fun detectConsecutivePatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        val sortedData = data.sortedBy { it.timestamp }
        
        var consecutiveGains = 0
        var consecutiveLosses = 0
        var maxGains = 0
        var maxLosses = 0
        var gainStreaks = 0
        var lossStreaks = 0
        
        sortedData.forEach { price ->
            val change = ((price.close - price.open) / price.open) * 100
            
            if (change > 0) {
                consecutiveGains++
                consecutiveLosses = 0
                if (consecutiveGains >= 3) {
                    gainStreaks++
                    maxGains = maxOf(maxGains, consecutiveGains)
                }
            } else if (change < 0) {
                consecutiveLosses++
                consecutiveGains = 0
                if (consecutiveLosses >= 3) {
                    lossStreaks++
                    maxLosses = maxOf(maxLosses, consecutiveLosses)
                }
            }
        }
        
        if (gainStreaks >= 3) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.CONSECUTIVE_GAINS,
                confidence = minOf(0.85, gainStreaks / 10.0),
                frequency = gainStreaks,
                lastOccurrence = sortedData.last().timestamp,
                predictedNextOccurrence = null,
                description = "Consecutive winning streak pattern: up to $maxGains days in a row, occurred $gainStreaks times",
                averageReturnPercentage = 0.0
            ))
        }
        
        if (lossStreaks >= 3) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.CONSECUTIVE_LOSSES,
                confidence = minOf(0.85, lossStreaks / 10.0),
                frequency = lossStreaks,
                lastOccurrence = sortedData.last().timestamp,
                predictedNextOccurrence = null,
                description = "Consecutive losing streak pattern: up to $maxLosses days in a row, occurred $lossStreaks times",
                averageReturnPercentage = 0.0
            ))
        }
        
        return patterns
    }
    
    /**
     * Detect price spike and drop patterns
     */
    private fun detectPriceMovementPatterns(data: List<PriceData>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        
        val priceChanges = data.map { ((it.close - it.open) / it.open) * 100 }
        val avgChange = priceChanges.map { abs(it) }.average()
        
        val spikes = data.zip(priceChanges).filter { it.second > avgChange * 2 }
        val drops = data.zip(priceChanges).filter { it.second < -avgChange * 2 }
        
        if (spikes.size >= 5) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.PRICE_SPIKE,
                confidence = 0.75,
                frequency = spikes.size,
                lastOccurrence = spikes.maxOf { it.first.timestamp },
                predictedNextOccurrence = null,
                description = "Significant price spikes (>${String.format("%.1f", avgChange * 2)}%) detected ${spikes.size} times",
                averageReturnPercentage = spikes.map { it.second }.average()
            ))
        }
        
        if (drops.size >= 5) {
            patterns.add(Pattern(
                symbol = data.first().symbol,
                patternType = PatternType.PRICE_DROP,
                confidence = 0.75,
                frequency = drops.size,
                lastOccurrence = drops.maxOf { it.first.timestamp },
                predictedNextOccurrence = null,
                description = "Significant price drops (<${String.format("%.1f", -avgChange * 2)}%) detected ${drops.size} times",
                averageReturnPercentage = drops.map { it.second }.average()
            ))
        }
        
        return patterns
    }
    
    // Helper functions
    
    private fun getHourOfDay(timestamp: Long): Int {
        return Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.HOUR_OF_DAY)
    }
    
    private fun getDayOfWeek(timestamp: Long): Int {
        return Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.DAY_OF_WEEK)
    }
    
    private fun getWeekOfYear(timestamp: Long): Int {
        return Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.WEEK_OF_YEAR)
    }
    
    private fun getMonthOfYear(timestamp: Long): Int {
        return Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.MONTH) + 1
    }
    
    private fun getQuarterOfYear(timestamp: Long): Int {
        val month = getMonthOfYear(timestamp)
        return (month - 1) / 3 + 1
    }
    
    private fun calculateAverageReturn(prices: List<PriceData>): Double {
        return prices.map { ((it.close - it.open) / it.open) * 100 }.average()
    }
    
    private fun calculateConsistency(prices: List<PriceData>): Double {
        val returns = prices.map { ((it.close - it.open) / it.open) * 100 }
        val mean = returns.average()
        val variance = returns.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        
        // Lower standard deviation = higher consistency
        return maxOf(0.0, 1.0 - (stdDev / 10.0))
    }
    
    private fun calculateMovingAverage(data: List<PriceData>, period: Int): List<Pair<Long, Double>> {
        return data.windowed(period).map { window ->
            window.last().timestamp to window.map { it.close }.average()
        }
    }
    
    private fun findCrossovers(ma1: List<Pair<Long, Double>>, ma2: List<Pair<Long, Double>>): List<Pair<Long, Boolean>> {
        val crossovers = mutableListOf<Pair<Long, Boolean>>()
        
        for (i in 1 until minOf(ma1.size, ma2.size)) {
            val prevDiff = ma1[i - 1].second - ma2[i - 1].second
            val currDiff = ma1[i].second - ma2[i].second
            
            if (prevDiff < 0 && currDiff > 0) {
                crossovers.add(ma1[i].first to true) // Bullish cross
            } else if (prevDiff > 0 && currDiff < 0) {
                crossovers.add(ma1[i].first to false) // Bearish cross
            }
        }
        
        return crossovers
    }
    
    private fun calculateAverageReturnAfterSignal(
        data: List<PriceData>,
        signals: List<Pair<Long, Boolean>>,
        daysAfter: Int
    ): Double {
        val returns = signals.mapNotNull { (timestamp, _) ->
            val index = data.indexOfFirst { it.timestamp >= timestamp }
            if (index >= 0 && index + daysAfter < data.size) {
                val startPrice = data[index].close
                val endPrice = data[index + daysAfter].close
                ((endPrice - startPrice) / startPrice) * 100
            } else null
        }
        
        return returns.takeIf { it.isNotEmpty() }?.average() ?: 0.0
    }
    
    private fun findRecurringLevels(prices: List<Double>, tolerance: Double): Map<Double, List<Int>> {
        val levels = mutableMapOf<Double, MutableList<Int>>()
        
        prices.forEachIndexed { index, price ->
            var foundLevel = false
            
            for ((level, indices) in levels) {
                if (abs(price - level) / level < tolerance) {
                    indices.add(index)
                    foundLevel = true
                    break
                }
            }
            
            if (!foundLevel) {
                levels[price] = mutableListOf(index)
            }
        }
        
        return levels.filter { it.value.size >= 3 }
    }
    
    private fun predictNextOccurrence(value: Int, type: String): Long {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        return when (type) {
            "hourly" -> {
                calendar.set(Calendar.HOUR_OF_DAY, value)
                if (calendar.timeInMillis < now) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                calendar.timeInMillis
            }
            "daily" -> {
                calendar.set(Calendar.DAY_OF_WEEK, value)
                if (calendar.timeInMillis < now) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }
                calendar.timeInMillis
            }
            "weekly" -> {
                calendar.set(Calendar.WEEK_OF_YEAR, value)
                if (calendar.timeInMillis < now) {
                    calendar.add(Calendar.YEAR, 1)
                }
                calendar.timeInMillis
            }
            "monthly" -> {
                calendar.set(Calendar.MONTH, value - 1)
                if (calendar.timeInMillis < now) {
                    calendar.add(Calendar.YEAR, 1)
                }
                calendar.timeInMillis
            }
            "quarterly" -> {
                val month = (value - 1) * 3
                calendar.set(Calendar.MONTH, month)
                if (calendar.timeInMillis < now) {
                    calendar.add(Calendar.YEAR, 1)
                }
                calendar.timeInMillis
            }
            else -> now + (7 * 24 * 60 * 60 * 1000) // Default: 1 week
        }
    }
    
    /**
     * Calculate RSI (Relative Strength Index)
     * Formula: RSI = 100 - (100 / (1 + RS))
     * where RS = Average Gain / Average Loss over period
     * 
     * @param prices Price data
     * @param period Look-back period (default 14)
     * @return RSI value (0-100), or 50.0 if insufficient data
     */
    private fun calculateRSI(prices: List<PriceData>, period: Int = 14): Double {
        if (prices.size < period + 1) return 50.0 // Neutral if insufficient data
        
        // Calculate price changes
        val changes = prices.zipWithNext { a, b -> b.close - a.close }
        
        // Take the last 'period' changes
        val recentChanges = changes.takeLast(period)
        
        // Separate gains and losses
        val gains = recentChanges.map { if (it > 0) it else 0.0 }
        val losses = recentChanges.map { if (it < 0) -it else 0.0 }
        
        // Calculate averages
        val avgGain = gains.average()
        val avgLoss = losses.average()
        
        // Handle edge case: no losses means RSI = 100
        if (avgLoss == 0.0) return 100.0
        
        // Calculate RS and RSI
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }
    
    /**
     * Detect RSI-based patterns
     * - Oversold: RSI < 30 (potential buy signal)
     * - Overbought: RSI > 70 (potential sell signal)
     * - Bullish Divergence: Price making lower lows while RSI makes higher lows
     * - Bearish Divergence: Price making higher highs while RSI makes lower highs
     */
    private fun detectRSIPatterns(data: List<PriceData>): List<Pattern> {
        if (data.size < 20) return emptyList()
        
        val patterns = mutableListOf<Pattern>()
        val rsi = calculateRSI(data)
        val symbol = data.firstOrNull()?.symbol ?: return emptyList()
        
        // Oversold condition (RSI < 30)
        if (rsi < 30.0) {
            val confidence = calculateRSIConfidence(rsi, isOversold = true)
            patterns.add(Pattern(
                symbol = symbol,
                patternType = PatternType.RSI_OVERSOLD,
                confidence = confidence,
                frequency = countRSIOversoldOccurrences(data),
                lastOccurrence = data.last().timestamp,
                predictedNextOccurrence = null,
                description = "RSI Oversold at ${String.format("%.1f", rsi)} - Strong potential for price bounce. " +
                        "Historically indicates reversal to the upside.",
                averageReturnPercentage = calculateAverageReturnAfterRSIOversold(data)
            ))
        }
        
        // Overbought condition (RSI > 70)
        if (rsi > 70.0) {
            val confidence = calculateRSIConfidence(rsi, isOversold = false)
            patterns.add(Pattern(
                symbol = symbol,
                patternType = PatternType.RSI_OVERBOUGHT,
                confidence = confidence,
                frequency = countRSIOverboughtOccurrences(data),
                lastOccurrence = data.last().timestamp,
                predictedNextOccurrence = null,
                description = "RSI Overbought at ${String.format("%.1f", rsi)} - Potential for price pullback. " +
                        "Historically indicates reversal to the downside.",
                averageReturnPercentage = calculateAverageReturnAfterRSIOverbought(data)
            ))
        }
        
        // Bullish Divergence detection
        val bullishDivergence = detectRSIBullishDivergence(data)
        if (bullishDivergence != null) {
            patterns.add(bullishDivergence)
        }
        
        // Bearish Divergence detection
        val bearishDivergence = detectRSIBearishDivergence(data)
        if (bearishDivergence != null) {
            patterns.add(bearishDivergence)
        }
        
        return patterns
    }
    
    /**
     * Calculate confidence for RSI signals
     * More extreme values = higher confidence
     */
    private fun calculateRSIConfidence(rsi: Double, isOversold: Boolean): Double {
        return if (isOversold) {
            // RSI 0-30: map to confidence 0.6-0.95
            0.95 - (rsi / 30.0 * 0.35)
        } else {
            // RSI 70-100: map to confidence 0.6-0.95
            0.6 + ((rsi - 70.0) / 30.0 * 0.35)
        }
    }
    
    /**
     * Count how many times RSI has been oversold in the dataset
     */
    private fun countRSIOversoldOccurrences(data: List<PriceData>): Int {
        var count = 0
        for (i in 14 until data.size) {
            val rsi = calculateRSI(data.take(i + 1))
            if (rsi < 30.0) count++
        }
        return count
    }
    
    /**
     * Count how many times RSI has been overbought in the dataset
     */
    private fun countRSIOverboughtOccurrences(data: List<PriceData>): Int {
        var count = 0
        for (i in 14 until data.size) {
            val rsi = calculateRSI(data.take(i + 1))
            if (rsi > 70.0) count++
        }
        return count
    }
    
    /**
     * Calculate average return after RSI oversold signals
     */
    private fun calculateAverageReturnAfterRSIOversold(data: List<PriceData>): Double {
        val returns = mutableListOf<Double>()
        
        for (i in 14 until data.size - 7) {
            val rsi = calculateRSI(data.take(i + 1))
            if (rsi < 30.0) {
                val entryPrice = data[i].close
                val exitPrice = data[i + 7].close
                val returnPct = ((exitPrice - entryPrice) / entryPrice) * 100.0
                returns.add(returnPct)
            }
        }
        
        return returns.takeIf { it.isNotEmpty() }?.average() ?: 0.0
    }
    
    /**
     * Calculate average return after RSI overbought signals
     */
    private fun calculateAverageReturnAfterRSIOverbought(data: List<PriceData>): Double {
        val returns = mutableListOf<Double>()
        
        for (i in 14 until data.size - 7) {
            val rsi = calculateRSI(data.take(i + 1))
            if (rsi > 70.0) {
                val entryPrice = data[i].close
                val exitPrice = data[i + 7].close
                val returnPct = ((exitPrice - entryPrice) / entryPrice) * 100.0
                returns.add(returnPct)
            }
        }
        
        return returns.takeIf { it.isNotEmpty() }?.average() ?: 0.0
    }
    
    /**
     * Detect bullish RSI divergence
     * Price makes lower lows while RSI makes higher lows
     */
    private fun detectRSIBullishDivergence(data: List<PriceData>): Pattern? {
        if (data.size < 30) return null
        
        val recent = data.takeLast(30)
        val symbol = data.firstOrNull()?.symbol ?: return null
        
        // Find price lows in recent period
        val priceLows = findLocalMinima(recent.map { it.close })
        if (priceLows.size < 2) return null
        
        // Calculate RSI at those points
        val rsiAtLows = priceLows.map { idx ->
            calculateRSI(data.take(data.size - 30 + idx + 1))
        }
        
        if (rsiAtLows.size < 2) return null
        
        // Check if price is making lower lows but RSI is making higher lows
        val priceDecreasing = recent[priceLows.last()].close < recent[priceLows[priceLows.size - 2]].close
        val rsiIncreasing = rsiAtLows.last() > rsiAtLows[rsiAtLows.size - 2]
        
        if (priceDecreasing && rsiIncreasing) {
            return Pattern(
                symbol = symbol,
                patternType = PatternType.RSI_BULLISH_DIVERGENCE,
                confidence = 0.75,
                frequency = 1,
                lastOccurrence = data.last().timestamp,
                predictedNextOccurrence = null,
                description = "Bullish RSI Divergence detected - Price making lower lows while RSI makes higher lows. " +
                        "Strong reversal signal suggesting upward momentum.",
                averageReturnPercentage = 3.5 // Typical divergence returns
            )
        }
        
        return null
    }
    
    /**
     * Detect bearish RSI divergence
     * Price makes higher highs while RSI makes lower highs
     */
    private fun detectRSIBearishDivergence(data: List<PriceData>): Pattern? {
        if (data.size < 30) return null
        
        val recent = data.takeLast(30)
        val symbol = data.firstOrNull()?.symbol ?: return null
        
        // Find price highs in recent period
        val priceHighs = findLocalMaxima(recent.map { it.close })
        if (priceHighs.size < 2) return null
        
        // Calculate RSI at those points
        val rsiAtHighs = priceHighs.map { idx ->
            calculateRSI(data.take(data.size - 30 + idx + 1))
        }
        
        if (rsiAtHighs.size < 2) return null
        
        // Check if price is making higher highs but RSI is making lower highs
        val priceIncreasing = recent[priceHighs.last()].close > recent[priceHighs[priceHighs.size - 2]].close
        val rsiDecreasing = rsiAtHighs.last() < rsiAtHighs[rsiAtHighs.size - 2]
        
        if (priceIncreasing && rsiDecreasing) {
            return Pattern(
                symbol = symbol,
                patternType = PatternType.RSI_BEARISH_DIVERGENCE,
                confidence = 0.75,
                frequency = 1,
                lastOccurrence = data.last().timestamp,
                predictedNextOccurrence = null,
                description = "Bearish RSI Divergence detected - Price making higher highs while RSI makes lower highs. " +
                        "Strong reversal signal suggesting downward momentum.",
                averageReturnPercentage = -3.5 // Typical divergence returns (negative)
            )
        }
        
        return null
    }
    
    /**
     * Find local minima (valley points) in a series
     */
    private fun findLocalMinima(values: List<Double>): List<Int> {
        val minima = mutableListOf<Int>()
        
        for (i in 1 until values.size - 1) {
            if (values[i] < values[i - 1] && values[i] < values[i + 1]) {
                minima.add(i)
            }
        }
        
        return minima
    }
    
    /**
     * Find local maxima (peak points) in a series
     */
    private fun findLocalMaxima(values: List<Double>): List<Int> {
        val maxima = mutableListOf<Int>()
        
        for (i in 1 until values.size - 1) {
            if (values[i] > values[i - 1] && values[i] > values[i + 1]) {
                maxima.add(i)
            }
        }
        
        return maxima
    }
    
    /**
     * Enhance existing patterns with RSI confirmation
     * Boosts confidence when RSI aligns with pattern prediction
     */
    private fun enhancePatternsWithRSI(patterns: MutableList<Pattern>, data: List<PriceData>) {
        if (data.size < 15) return
        
        val currentRSI = calculateRSI(data)
        
        for (i in patterns.indices) {
            val pattern = patterns[i]
            var confidenceBoost = 0.0
            
            // Bullish patterns get boost from oversold RSI
            when (pattern.patternType) {
                PatternType.MOVING_AVERAGE_CROSS -> {
                    if (pattern.averageReturnPercentage > 0 && currentRSI < 40.0) {
                        confidenceBoost = 0.15 // 15% boost for bullish MA cross with low RSI
                    } else if (pattern.averageReturnPercentage < 0 && currentRSI > 60.0) {
                        confidenceBoost = 0.15 // 15% boost for bearish MA cross with high RSI
                    }
                }
                PatternType.SUPPORT_LEVEL -> {
                    if (currentRSI < 35.0) {
                        confidenceBoost = 0.10 // Support level more reliable with oversold RSI
                    }
                }
                PatternType.RESISTANCE_LEVEL -> {
                    if (currentRSI > 65.0) {
                        confidenceBoost = 0.10 // Resistance level more reliable with overbought RSI
                    }
                }
                PatternType.PRICE_SPIKE -> {
                    if (currentRSI > 70.0) {
                        confidenceBoost = 0.12 // Price spike with overbought = likely reversal
                    }
                }
                PatternType.PRICE_DROP -> {
                    if (currentRSI < 30.0) {
                        confidenceBoost = 0.12 // Price drop with oversold = likely bounce
                    }
                }
                else -> {
                    // General boost for patterns aligned with RSI
                    if (pattern.averageReturnPercentage > 0 && currentRSI < 45.0) {
                        confidenceBoost = 0.08
                    } else if (pattern.averageReturnPercentage < 0 && currentRSI > 55.0) {
                        confidenceBoost = 0.08
                    }
                }
            }
            
            if (confidenceBoost > 0) {
                // Update pattern with boosted confidence
                patterns[i] = pattern.copy(
                    confidence = minOf(0.99, pattern.confidence + confidenceBoost),
                    description = pattern.description + " [RSI Confirmed: ${String.format("%.1f", currentRSI)}]"
                )
            }
        }
    }
}
