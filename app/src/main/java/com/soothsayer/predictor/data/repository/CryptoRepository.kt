package com.soothsayer.predictor.data.repository

import com.soothsayer.predictor.data.local.CryptoAssetDao
import com.soothsayer.predictor.data.local.PatternDao
import com.soothsayer.predictor.data.local.PriceDataDao
import com.soothsayer.predictor.data.models.CryptoAsset
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PriceData
import com.soothsayer.predictor.data.models.Resource
import com.soothsayer.predictor.data.remote.BinanceApi
import com.soothsayer.predictor.data.remote.CoinGeckoApi
import com.soothsayer.predictor.data.remote.CryptoCompareApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main repository for crypto data
 * 
 * Data Strategy:
 * 1. Try Binance (primary, fastest, most reliable)
 * 2. Fallback to CoinGecko (free, good coverage)
 * 3. Fallback to CryptoCompare (backup)
 * 4. Use cached data if all fail
 * 
 * Storage Strategy:
 * - Local: Last 90 days of daily data (~50KB for 10 cryptos)
 * - Cloud: Full historical data (optional sync)
 * - Patterns: All patterns stored locally (small footprint)
 */
@Singleton
class CryptoRepository @Inject constructor(
    private val binanceApi: BinanceApi,
    private val coinGeckoApi: CoinGeckoApi,
    private val cryptoCompareApi: CryptoCompareApi,
    private val priceDataDao: PriceDataDao,
    private val cryptoAssetDao: CryptoAssetDao,
    private val patternDao: PatternDao
) {
    
    companion object {
        private const val CACHE_VALIDITY_HOURS = 24
        private const val DATA_RETENTION_DAYS = 90 // Keep only 90 days locally
        private val SUPPORTED_SYMBOLS = listOf(
            "BTCUSDT" to "Bitcoin",
            "ETHUSDT" to "Ethereum",
            "BNBUSDT" to "Binance Coin",
            "ADAUSDT" to "Cardano",
            "XRPUSDT" to "Ripple",
            "SOLUSDT" to "Solana",
            "DOTUSDT" to "Polkadot",
            "DOGEUSDT" to "Dogecoin",
            "AVAXUSDT" to "Avalanche",
            "MATICUSDT" to "Polygon",
            "OCEANUSDT" to "Ocean Protocol"
        )
    }
    
    /**
     * Get price history with multi-source fallback
     */
    suspend fun getPriceHistory(
        symbol: String,
        days: Int = 365,
        forceRefresh: Boolean = false
    ): Resource<List<PriceData>> = withContext(Dispatchers.IO) {
        android.util.Log.d("CryptoRepository", "getPriceHistory called for $symbol, days=$days, forceRefresh=$forceRefresh")
        try {
            // Check cache first
            if (!forceRefresh) {
                val cached = priceDataDao.getPriceData(
                    symbol = symbol,
                    startTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
                )
                
                if (cached.isNotEmpty() && !isCacheStale(cached)) {
                    android.util.Log.d("CryptoRepository", "Returning cached data for $symbol: ${cached.size} points")
                    return@withContext Resource.Success(cached)
                }
            }
            
            // Try fetching from APIs (Binance -> CoinGecko -> CryptoCompare)
            android.util.Log.d("CryptoRepository", "Fetching fresh data for $symbol")
            val freshData = fetchFromBinance(symbol, days)?.takeIf { it.isNotEmpty() }
                ?: fetchFromCoinGecko(symbol, days)?.takeIf { it.isNotEmpty() }
                ?: fetchFromCryptoCompare(symbol, days)?.takeIf { it.isNotEmpty() }
                ?: return@withContext Resource.Error("Failed to fetch data from all sources")
            
            android.util.Log.d("CryptoRepository", "Fetched ${freshData.size} data points for $symbol")
            
            // Save to database
            priceDataDao.insertPriceDataBatch(freshData)
            
            // Cleanup old data to keep database small
            cleanupOldData(symbol)
            
            Resource.Success(freshData)
            
        } catch (e: Exception) {
            android.util.Log.e("CryptoRepository", "Error in getPriceHistory for $symbol", e)
            // Return cached data if available
            val cached = priceDataDao.getPriceData(symbol)
            if (cached.isNotEmpty()) {
                android.util.Log.d("CryptoRepository", "Returning cached data due to error for $symbol: ${cached.size} points")
                Resource.Success(cached)
            } else {
                Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    /**
     * Fetch from Binance (PRIMARY SOURCE)
     * Fastest, most reliable, no API key needed
     */
    private suspend fun fetchFromBinance(symbol: String, days: Int): List<PriceData>? {
        return try {
            val startTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
            val endTime = System.currentTimeMillis()
            
            val response = binanceApi.getKlines(
                symbol = symbol,
                interval = "1d",
                limit = minOf(days, 1000),
                startTime = startTime,
                endTime = endTime
            )
            
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { kline ->
                    PriceData(
                        symbol = symbol,
                        timestamp = (kline[0] as Double).toLong(),
                        open = (kline[1] as String).toDouble(),
                        high = (kline[2] as String).toDouble(),
                        low = (kline[3] as String).toDouble(),
                        close = (kline[4] as String).toDouble(),
                        volume = (kline[5] as String).toDouble()
                    )
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fetch from CoinGecko (FALLBACK SOURCE)
     * Free tier, good for when Binance is unavailable
     */
    private suspend fun fetchFromCoinGecko(symbol: String, days: Int): List<PriceData>? {
        return try {
            val coinId = mapSymbolToCoinGeckoId(symbol)
            android.util.Log.d("CryptoRepository", "fetchFromCoinGecko called for $symbol -> coinId: $coinId")
            
            // TEMP: Return dummy data for Ocean to test chart display
            if (coinId == "ocean") {
                android.util.Log.d("CryptoRepository", "Returning dummy data for Ocean")
                return createDummyDataForOcean(symbol)
            }
            
            val response = coinGeckoApi.getMarketChart(
                id = coinId,
                vsCurrency = "usd",
                days = minOf(days, 30)  // Limit to 30 days to ensure data availability
            )
            
            android.util.Log.d("CryptoRepository", "CoinGecko response for $symbol: success=${response.isSuccessful}, body=${response.body() != null}")
            
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val result = data.prices.mapIndexed { index, price ->
                    PriceData(
                        symbol = symbol,
                        timestamp = price[0].toLong(),
                        open = price[1],
                        high = price[1],  // Use same price for OHLC approximation
                        low = price[1],   // Use same price for OHLC approximation
                        close = price[1],
                        volume = data.total_volumes.getOrNull(index)?.get(1) ?: 0.0
                    )
                }.sortedBy { it.timestamp }  // Ensure data is sorted by timestamp
                
                android.util.Log.d("CryptoRepository", "Parsed ${result.size} data points from CoinGecko for $symbol")
                result
            } else {
                android.util.Log.w("CryptoRepository", "CoinGecko failed for $symbol: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("CryptoRepository", "Exception in fetchFromCoinGecko for $symbol", e)
            null
        }
    }
    
    /**
     * Fetch from CryptoCompare (BACKUP SOURCE)
     * Requires API key but free tier available
     */
    private suspend fun fetchFromCryptoCompare(symbol: String, days: Int): List<PriceData>? {
        return try {
            val fsym = symbol.replace("USDT", "")
            val response = cryptoCompareApi.getHistoricalDaily(
                fsym = fsym,
                tsym = "USD",
                limit = days
            )
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                @Suppress("UNCHECKED_CAST")
                val dataArray = body["Data"] as? Map<String, Any>
                @Suppress("UNCHECKED_CAST")
                val data = dataArray?.get("Data") as? List<Map<String, Any>>
                
                data?.map { item ->
                    PriceData(
                        symbol = symbol,
                        timestamp = (item["time"] as Double).toLong() * 1000,
                        open = (item["open"] as? Double) ?: 0.0,
                        high = (item["high"] as? Double) ?: 0.0,
                        low = (item["low"] as? Double) ?: 0.0,
                        close = (item["close"] as? Double) ?: 0.0,
                        volume = (item["volumefrom"] as? Double) ?: 0.0
                    )
                } ?: emptyList()
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get supported crypto assets
     */
    suspend fun getSupportedAssets(): Resource<List<CryptoAsset>> = withContext(Dispatchers.IO) {
        try {
            // Check if we have assets in DB
            val cachedAssets = cryptoAssetDao.getAllAssets()
            if (cachedAssets.isNotEmpty()) {
                return@withContext Resource.Success(cachedAssets)
            }
            
            // Initialize with predefined list
            val assets = SUPPORTED_SYMBOLS.map { (symbol, name) ->
                CryptoAsset(
                    symbol = symbol,
                    name = name,
                    currentPrice = 0.0,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            
            cryptoAssetDao.insertAssets(assets)
            Resource.Success(assets)
            
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load assets")
        }
    }
    
    /**
     * Save detected patterns
     */
    suspend fun savePatterns(patterns: List<Pattern>): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            patternDao.insertPatterns(patterns)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save patterns")
        }
    }
    
    /**
     * Get patterns for a symbol
     */
    suspend fun getPatterns(
        symbol: String,
        minConfidence: Double = 0.0
    ): Resource<List<Pattern>> = withContext(Dispatchers.IO) {
        try {
            val patterns = if (minConfidence > 0.0) {
                patternDao.getHighConfidencePatterns(symbol, minConfidence)
            } else {
                patternDao.getPatternsForSymbol(symbol)
            }
            Resource.Success(patterns)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load patterns")
        }
    }
    
    /**
     * Cleanup old data to keep database small
     * Keeps only last 90 days of daily data
     */
    private suspend fun cleanupOldData(symbol: String) {
        try {
            val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(DATA_RETENTION_DAYS.toLong())
            priceDataDao.cleanupOldData(symbol, cutoffTime)
        } catch (e: Exception) {
            // Log error but don't fail
        }
    }
    
    /**
     * Check if cache is stale (older than 24 hours)
     */
    private fun isCacheStale(data: List<PriceData>): Boolean {
        if (data.isEmpty()) return true
        val latestTimestamp = data.maxOf { it.timestamp }
        val hoursSinceUpdate = TimeUnit.MILLISECONDS.toHours(
            System.currentTimeMillis() - latestTimestamp
        )
        return hoursSinceUpdate > CACHE_VALIDITY_HOURS
    }
    
    /**
     * Create dummy data for Ocean Protocol testing
     */
    private fun createDummyDataForOcean(symbol: String): List<PriceData> {
        val now = System.currentTimeMillis()
        return (0..30).map { daysAgo ->
            val timestamp = now - (daysAgo * 24 * 60 * 60 * 1000L)
            val basePrice = 0.5 + (Math.random() - 0.5) * 0.2  // Random price around $0.50
            PriceData(
                symbol = symbol,
                timestamp = timestamp,
                open = basePrice,
                high = basePrice * 1.05,
                low = basePrice * 0.95,
                close = basePrice,
                volume = 1000000.0 + Math.random() * 500000.0
            )
        }.sortedBy { it.timestamp }
    }
    
    /**
     * Map Binance symbol to CoinGecko ID
     */
    private fun mapSymbolToCoinGeckoId(symbol: String): String {
        return when (symbol) {
            "BTCUSDT" -> "bitcoin"
            "ETHUSDT" -> "ethereum"
            "BNBUSDT" -> "binancecoin"
            "ADAUSDT" -> "cardano"
            "XRPUSDT" -> "ripple"
            "SOLUSDT" -> "solana"
            "DOTUSDT" -> "polkadot"
            "DOGEUSDT" -> "dogecoin"
            "AVAXUSDT" -> "avalanche-2"
            "MATICUSDT" -> "matic-network"
            "OCEANUSDT" -> "ocean"
            else -> symbol.replace("USDT", "").lowercase()
        }
    }
    
    /**
     * Get database size info
     */
    suspend fun getDatabaseStats(): Map<String, Any> = withContext(Dispatchers.IO) {
        val stats = mutableMapOf<String, Any>()
        
        SUPPORTED_SYMBOLS.forEach { (symbol, _) ->
            val count = priceDataDao.getRecordCount(symbol)
            stats[symbol] = count
        }
        
        stats
    }
}
