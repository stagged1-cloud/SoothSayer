package com.soothsayer.predictor.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * CryptoCompare API client (TERTIARY DATA SOURCE)
 * Free tier: 100,000 calls/month
 * API key required (free registration)
 */
interface CryptoCompareApi {
    
    /**
     * Get historical daily OHLCV data
     * @param fsym From symbol (e.g., "BTC")
     * @param tsym To symbol (e.g., "USD")
     * @param limit Number of data points (max 2000)
     * @param toTs Timestamp to get data up to
     */
    @GET("/data/v2/histoday")
    suspend fun getHistoricalDaily(
        @Query("fsym") fsym: String,
        @Query("tsym") tsym: String = "USD",
        @Query("limit") limit: Int = 365,
        @Query("toTs") toTs: Long? = null
    ): Response<Map<String, Any>>
    
    /**
     * Get historical hourly OHLCV data
     */
    @GET("/data/v2/histohour")
    suspend fun getHistoricalHourly(
        @Query("fsym") fsym: String,
        @Query("tsym") tsym: String = "USD",
        @Query("limit") limit: Int = 168,
        @Query("toTs") toTs: Long? = null
    ): Response<Map<String, Any>>
    
    /**
     * Get current price
     */
    @GET("/data/price")
    suspend fun getCurrentPrice(
        @Query("fsym") fsym: String,
        @Query("tsyms") tsyms: String = "USD"
    ): Response<Map<String, Double>>
}
