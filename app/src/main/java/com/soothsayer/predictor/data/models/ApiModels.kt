package com.soothsayer.predictor.data.models

/**
 * API Response models for Binance
 */
data class BinanceKlineResponse(
    val openTime: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val closeTime: Long,
    val quoteAssetVolume: String,
    val numberOfTrades: Int,
    val takerBuyBaseAssetVolume: String,
    val takerBuyQuoteAssetVolume: String,
    val ignore: String
) {
    fun toPriceData(symbol: String): PriceData {
        return PriceData(
            symbol = symbol,
            timestamp = openTime,
            open = open.toDouble(),
            high = high.toDouble(),
            low = low.toDouble(),
            close = close.toDouble(),
            volume = volume.toDouble()
        )
    }
}

/**
 * CoinGecko market chart response
 */
data class CoinGeckoMarketChartResponse(
    val prices: List<List<Double>>,
    val market_caps: List<List<Double>>,
    val total_volumes: List<List<Double>>
)

/**
 * Generic API response wrapper
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

/**
 * Resource wrapper for UI state management
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
