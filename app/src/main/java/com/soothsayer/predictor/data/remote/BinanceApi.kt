package com.soothsayer.predictor.data.remote

import com.soothsayer.predictor.data.models.BinanceKlineResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Binance API client (PRIMARY DATA SOURCE)
 * Free, no API key required for public endpoints
 * Rate limit: 1200 requests/minute
 */
interface BinanceApi {
    
    /**
     * Get kline/candlestick bars for a symbol
     * @param symbol Trading pair (e.g., "BTCUSDT")
     * @param interval Kline interval (1m, 5m, 15m, 1h, 4h, 1d, 1w, 1M)
     * @param limit Number of records (max 1000, default 500)
     * @param startTime Start time in milliseconds
     * @param endTime End time in milliseconds
     */
    @GET("/api/v3/klines")
    suspend fun getKlines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("limit") limit: Int = 1000,
        @Query("startTime") startTime: Long? = null,
        @Query("endTime") endTime: Long? = null
    ): Response<List<List<Any>>>
    
    /**
     * Get 24hr ticker price change statistics
     */
    @GET("/api/v3/ticker/24hr")
    suspend fun get24hrTicker(
        @Query("symbol") symbol: String
    ): Response<Map<String, Any>>
    
    /**
     * Get latest price for a symbol
     */
    @GET("/api/v3/ticker/price")
    suspend fun getPrice(
        @Query("symbol") symbol: String
    ): Response<Map<String, String>>
    
    /**
     * Get all trading pairs
     */
    @GET("/api/v3/exchangeInfo")
    suspend fun getExchangeInfo(): Response<Map<String, Any>>
}
