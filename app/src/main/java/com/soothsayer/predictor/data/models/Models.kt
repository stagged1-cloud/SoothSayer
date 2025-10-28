package com.soothsayer.predictor.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Cryptocurrency asset model
 */
@Entity(tableName = "crypto_assets")
data class CryptoAsset(
    @PrimaryKey val symbol: String,
    val name: String,
    val currentPrice: Double,
    val lastUpdated: Long
)

/**
 * OHLCV (Open, High, Low, Close, Volume) price data
 */
@Parcelize
@Entity(tableName = "price_data")
data class PriceData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
) : Parcelable

/**
 * Detected pattern in crypto data
 */
@Parcelize
@Entity(tableName = "patterns")
data class Pattern(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val patternType: PatternType,
    val confidence: Double,
    val frequency: Int,
    val lastOccurrence: Long,
    val predictedNextOccurrence: Long?,
    val description: String,
    val averageReturnPercentage: Double
) : Parcelable

/**
 * Pattern types for detection
 */
enum class PatternType {
    // Time-based patterns
    HOURLY_SPIKE,
    HOURLY_DROP,
    DAILY_PATTERN,
    WEEKLY_PATTERN,
    MONTHLY_PATTERN,
    YEARLY_PATTERN,
    
    // Price movement patterns
    PRICE_SPIKE,
    PRICE_DROP,
    CONSOLIDATION,
    BREAKOUT,
    
    // Technical patterns
    MOVING_AVERAGE_CROSS,
    SUPPORT_LEVEL,
    RESISTANCE_LEVEL,
    VOLUME_SPIKE,
    
    // Volatility patterns
    HIGH_VOLATILITY,
    LOW_VOLATILITY,
    
    // Consecutive patterns
    CONSECUTIVE_GAINS,
    CONSECUTIVE_LOSSES,
    
    // Seasonal
    SEASONAL_TREND,
    QUARTERLY_PATTERN
}

/**
 * Filter options for pattern detection
 */
data class PatternFilter(
    val enableHourlyAnalysis: Boolean = false,
    val enableDailyAnalysis: Boolean = true,
    val enableWeeklyAnalysis: Boolean = true,
    val enableMonthlyAnalysis: Boolean = true,
    val enableYearlyAnalysis: Boolean = false,
    val enableMovingAverages: Boolean = true,
    val enableVolumeCorrelation: Boolean = true,
    val enableVolatilityAnalysis: Boolean = false,
    val enableSupportResistance: Boolean = true,
    val enableSeasonalTrends: Boolean = false,
    val minimumConfidence: Double = 0.6,
    val minimumFrequency: Int = 3
)

/**
 * Pattern analysis result
 */
data class PatternAnalysisResult(
    val symbol: String,
    val patterns: List<Pattern>,
    val totalPatternsDetected: Int,
    val analysisTimestamp: Long,
    val dataRangeStart: Long,
    val dataRangeEnd: Long
)
