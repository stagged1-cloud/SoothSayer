package com.soothsayer.predictor.domain.usecases

import com.soothsayer.predictor.analysis.PatternAnalyzer
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PatternFilter
import com.soothsayer.predictor.data.models.Resource
import com.soothsayer.predictor.data.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for detecting patterns in crypto price data
 * Coordinates data fetching and pattern analysis
 */
class DetectPatternsUseCase @Inject constructor(
    private val repository: CryptoRepository,
    private val patternAnalyzer: PatternAnalyzer
) {
    
    /**
     * Execute pattern detection
     * 
     * @param symbol Crypto symbol (e.g., "BTCUSDT")
     * @param filters Pattern detection filters
     * @param days Number of days of historical data to analyze
     * @return Resource containing detected patterns
     */
    suspend operator fun invoke(
        symbol: String,
        filters: PatternFilter,
        days: Int = 365
    ): Resource<List<Pattern>> = withContext(Dispatchers.Default) {
        
        try {
            // Step 1: Fetch price data
            val priceDataResource = repository.getPriceHistory(symbol, days)
            
            if (priceDataResource is Resource.Error) {
                return@withContext Resource.Error(priceDataResource.message ?: "Failed to fetch data")
            }
            
            val priceData = priceDataResource.data ?: return@withContext Resource.Error("No data available")
            
            if (priceData.isEmpty()) {
                return@withContext Resource.Error("Insufficient data for analysis")
            }
            
            // Step 2: Analyze patterns
            val patterns = patternAnalyzer.analyze(priceData, filters)
            
            // Step 3: Save patterns to database
            if (patterns.isNotEmpty()) {
                repository.savePatterns(patterns)
            }
            
            // Step 4: Return results
            Resource.Success(patterns)
            
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Pattern detection failed")
        }
    }
    
    /**
     * Get cached patterns without re-analyzing
     */
    suspend fun getCachedPatterns(
        symbol: String,
        minConfidence: Double = 0.6
    ): Resource<List<Pattern>> {
        return repository.getPatterns(symbol, minConfidence)
    }
    
    /**
     * Get price data for charting
     */
    suspend fun getPriceData(
        symbol: String,
        days: Int = 90
    ): Resource<List<com.soothsayer.predictor.data.models.PriceData>> {
        return repository.getPriceHistory(symbol, days)
    }
}
