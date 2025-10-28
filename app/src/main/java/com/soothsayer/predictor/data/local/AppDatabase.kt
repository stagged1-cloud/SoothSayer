package com.soothsayer.predictor.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soothsayer.predictor.data.models.CryptoAsset
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PriceData

/**
 * Room Database for SoothSayer
 * 
 * Storage Strategy:
 * - Keep only essential data locally (last 90 days of daily data)
 * - Store aggregated data for older periods
 * - Patterns are stored indefinitely (small footprint)
 * - Cloud sync for full historical data
 * 
 * Estimated size for 10 cryptos, 90 days:
 * - PriceData: ~900 records x 40 bytes = ~36KB
 * - Patterns: ~50 records x 100 bytes = ~5KB
 * - Total: ~50KB (very efficient)
 */
@Database(
    entities = [
        CryptoAsset::class,
        PriceData::class,
        Pattern::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cryptoAssetDao(): CryptoAssetDao
    abstract fun priceDataDao(): PriceDataDao
    abstract fun patternDao(): PatternDao
    
    companion object {
        const val DATABASE_NAME = "soothsayer.db"
    }
}

/**
 * DAO for cryptocurrency assets
 */
@Dao
interface CryptoAssetDao {
    
    @Query("SELECT * FROM crypto_assets ORDER BY name ASC")
    suspend fun getAllAssets(): List<CryptoAsset>
    
    @Query("SELECT * FROM crypto_assets WHERE symbol = :symbol LIMIT 1")
    suspend fun getAsset(symbol: String): CryptoAsset?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: CryptoAsset)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<CryptoAsset>)
    
    @Query("DELETE FROM crypto_assets WHERE symbol = :symbol")
    suspend fun deleteAsset(symbol: String)
    
    @Query("UPDATE crypto_assets SET currentPrice = :price, lastUpdated = :timestamp WHERE symbol = :symbol")
    suspend fun updatePrice(symbol: String, price: Double, timestamp: Long)
}

/**
 * DAO for price data (OHLCV)
 */
@Dao
interface PriceDataDao {
    
    @Query("SELECT * FROM price_data WHERE symbol = :symbol AND timestamp >= :startTime ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getPriceData(symbol: String, startTime: Long = 0, limit: Int = 1000): List<PriceData>
    
    @Query("SELECT * FROM price_data WHERE symbol = :symbol AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getPriceDataRange(symbol: String, startTime: Long, endTime: Long): List<PriceData>
    
    @Query("SELECT * FROM price_data WHERE symbol = :symbol ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestPrice(symbol: String): PriceData?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceData(priceData: PriceData)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceDataBatch(priceData: List<PriceData>)
    
    @Query("DELETE FROM price_data WHERE symbol = :symbol AND timestamp < :cutoffTime")
    suspend fun deleteOldData(symbol: String, cutoffTime: Long): Int
    
    @Query("DELETE FROM price_data WHERE symbol = :symbol")
    suspend fun deleteAllForSymbol(symbol: String)
    
    @Query("SELECT COUNT(*) FROM price_data WHERE symbol = :symbol")
    suspend fun getRecordCount(symbol: String): Int
    
    /**
     * Clean up old data (keep only last 90 days of daily data)
     * This keeps database size minimal
     */
    @Query("""
        DELETE FROM price_data 
        WHERE timestamp < :cutoffTime 
        AND symbol = :symbol
    """)
    suspend fun cleanupOldData(symbol: String, cutoffTime: Long)
}

/**
 * DAO for detected patterns
 */
@Dao
interface PatternDao {
    
    @Query("SELECT * FROM patterns WHERE symbol = :symbol ORDER BY confidence DESC")
    suspend fun getPatternsForSymbol(symbol: String): List<Pattern>
    
    @Query("SELECT * FROM patterns WHERE symbol = :symbol AND confidence >= :minConfidence ORDER BY confidence DESC")
    suspend fun getHighConfidencePatterns(symbol: String, minConfidence: Double): List<Pattern>
    
    @Query("SELECT * FROM patterns WHERE patternType = :type ORDER BY confidence DESC")
    suspend fun getPatternsByType(type: String): List<Pattern>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: Pattern)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatterns(patterns: List<Pattern>)
    
    @Query("DELETE FROM patterns WHERE symbol = :symbol")
    suspend fun deletePatternsForSymbol(symbol: String)
    
    @Query("DELETE FROM patterns WHERE id = :patternId")
    suspend fun deletePattern(patternId: Long)
    
    @Query("DELETE FROM patterns WHERE confidence < :minConfidence")
    suspend fun deleteLowConfidencePatterns(minConfidence: Double)
    
    /**
     * Get patterns that are due soon (for notifications)
     */
    @Query("""
        SELECT * FROM patterns 
        WHERE predictedNextOccurrence IS NOT NULL 
        AND predictedNextOccurrence BETWEEN :startTime AND :endTime
        ORDER BY predictedNextOccurrence ASC
    """)
    suspend fun getUpcomingPatterns(startTime: Long, endTime: Long): List<Pattern>
}
