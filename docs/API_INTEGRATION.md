# API Integration Guide

## Overview
SoothSayer integrates with multiple cryptocurrency data providers to ensure reliable and comprehensive historical price data.

## Data Sources

### . Binance API (Primary Source)
**Base URL**: `https://api.binance.com`

#### Endpoints Used:
- **Klines/Candlestick Data**: `/api/v/klines`
  - Provides OHLCV (Open, High, Low, Close, Volume) data
  - Historical data available up to 000 records per request
  - Intervals: m, m, 5m, 5m, 0m, h, h, 4h, 6h, 8h, h, d, d, w, M

**Example Request**:
```
GET /api/v/klines?symbol=BTCUSDT&interval=d&limit=000
```

**Rate Limits**:
- Weight: 
- Limit: 00 requests per minute
- No API key required for public endpoints (recommended for higher limits)

**Data Format**:
```json
[
  [
    499040000000,      // Open time
    "0.064790",       // Open
    "0.80000000",       // High
    "0.0575800",       // Low
    "0.057700",       // Close
    "48976.4785",  // Volume
    499644799999,      // Close time
    "44.90554",    // Quote asset volume
    08,                // Number of trades
    "756.874097",    // Taker buy base volume
    "8.4669468",      // Taker buy quote volume
    "798899.64849" // Ignore
  ]
]
```

### . CoinGecko API (Secondary Source)
**Base URL**: `https://api.coingecko.com/api/v/`

#### Endpoints Used:
- **Market Chart**: `/coins/{id}/market_chart`
  - Historical market data including price, market cap, volume
  - Range:  day to max available
  
**Example Request**:
```
GET /coins/bitcoin/market_chart?vs_currency=usd&days=65
```

**Rate Limits**:
- Free tier: 0-50 calls/minute
- No API key required for basic usage

**Data Format**:
```json
{
  "prices": [[680600000, 58.], ...],
  "market_caps": [[680600000, 674567890], ...],
  "total_volumes": [[680600000, 45456789], ...]
}
```

### . CryptoCompare API (Tertiary Source)
**Base URL**: `https://min-api.cryptocompare.com`

#### Endpoints Used:
- **Historical Daily**: `/data/v/histoday`
- **Historical Hourly**: `/data/v/histohour`

**Example Request**:
```
GET /data/v/histoday?fsym=BTC&tsym=USD&limit=65
```

**Rate Limits**:
- Free tier: 00,000 calls/month
- API key required (free registration)

## Implementation Strategy

### Data Fetching Priority
. **Binance** (primary) - Most reliable and comprehensive
. **CoinGecko** (fallback) - If Binance data unavailable
. **CryptoCompare** (backup) - Alternative source

### Caching Strategy
- Cache data locally using Room database
- Update frequency:
  - Real-time data: Every 5 minutes
  - Historical data: Once per day
  - Pattern analysis: On-demand with -hour cache

### Error Handling
```kotlin
sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val message: String, val code: Int?) : ApiResult<T>()
    object NetworkError : ApiResult<Nothing>()
    object RateLimitExceeded : ApiResult<Nothing>()
}
```

## API Client Configuration

### Retrofit Setup
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
    
    @Provides
    @Singleton
    @Named("binance")
    fun provideBinanceRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BINANCE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

## Data Synchronization

### Initial Data Load
. Fetch last 000 days of daily data
. Store in local database
. Calculate initial patterns

### Incremental Updates
. Fetch data since last update
. Merge with existing data
. Re-run pattern detection on new data

## Security Considerations

### API Keys
- Store in `local.properties` (not committed to git)
- Access via BuildConfig
- Never hardcode in source files

### HTTPS Only
- All API calls use HTTPS
- Certificate pinning for production
- No cleartext traffic allowed

## Testing

### Mock Responses
Use MockWebServer for testing:
```kotlin
@Test
fun testBinanceKlinesResponse() {
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(00)
            .setBody(readTestResource("binance_klines.json"))
    )
    // Test implementation
}
```

## Rate Limit Management

### Strategies
. **Request Batching**: Combine multiple symbol requests
. **Caching**: Aggressive local caching to minimize API calls
. **Retry Logic**: Exponential backoff on rate limit errors
4. **Queue System**: Request queue with rate limiting

### Implementation
```kotlin
class RateLimiter(
    private val maxRequests: Int,
    private val timeWindow: Long
) {
    private val timestamps = mutableListOf<Long>()
    
    suspend fun <T> execute(block: suspend () -> T): T {
        waitIfNeeded()
        return block()
    }
    
    private suspend fun waitIfNeeded() {
        val now = System.currentTimeMillis()
        timestamps.removeIf { it < now - timeWindow }
        
        if (timestamps.size >= maxRequests) {
            val oldestTimestamp = timestamps.first()
            val waitTime = timeWindow - (now - oldestTimestamp)
            delay(waitTime)
        }
        
        timestamps.add(now)
    }
}
```

## Supported Cryptocurrencies

### Initial Release
- BTC (Bitcoin)
- ETH (Ethereum)
- BNB (Binance Coin)
- ADA (Cardano)
- XRP (Ripple)
- SOL (Solana)
- DOT (Polkadot)
- DOGE (Dogecoin)
- AVAX (Avalanche)
- MATIC (Polygon)

### Future Additions
- User can search and add any available cryptocurrency
- Support for 500+ assets across all platforms

## Error Codes

| Code | Description | Action |
|------|-------------|--------|
| 00 | Success | Process data |
| 400 | Bad Request | Check parameters |
| 40 | Unauthorized | Verify API key |
| 49 | Rate Limit | Implement backoff |
| 500 | Server Error | Retry with fallback |
| 50 | Service Unavailable | Use cache/fallback |

## Monitoring & Analytics

### Metrics to Track
- API response times
- Success/error rates
- Cache hit rates
- Data freshness
- Pattern detection performance

### Logging
```kotlin
class ApiLogger {
    fun logRequest(endpoint: String, params: Map<String, Any>)
    fun logResponse(endpoint: String, responseTime: Long, success: Boolean)
    fun logError(endpoint: String, error: Throwable)
}
```
