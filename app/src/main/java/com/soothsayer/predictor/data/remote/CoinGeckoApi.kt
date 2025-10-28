package com.soothsayer.predictor.data.remote

import com.soothsayer.predictor.data.models.CoinGeckoMarketChartResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * CoinGecko API client (FALLBACK DATA SOURCE)
 * Free tier: 10-50 calls/minute
 * No API key required for basic usage
 */
interface CoinGeckoApi {
    
    /**
     * Get historical market data including price, market cap, and 24h volume
     * @param id Coin id (e.g., "bitcoin", "ethereum")
     * @param vsCurrency Target currency (e.g., "usd")
     * @param days Data up to number of days ago (1/7/14/30/90/180/365/max)
     * @param interval Data interval (auto/daily)
     */
    @GET("/api/v3/coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 365,
        @Query("interval") interval: String? = null
    ): Response<CoinGeckoMarketChartResponse>
    
    /**
     * Get current data for a coin
     */
    @GET("/api/v3/coins/{id}")
    suspend fun getCoinData(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false
    ): Response<Map<String, Any>>
    
    /**
     * Get list of all supported coins
     */
    @GET("/api/v3/coins/list")
    suspend fun getCoinsList(
        @Query("include_platform") includePlatform: Boolean = false
    ): Response<List<Map<String, Any>>>
}
