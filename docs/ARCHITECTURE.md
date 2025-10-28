# SoothSayer Architecture

## Overview
SoothSayer follows Clean Architecture principles with MVVM pattern for the presentation layer, ensuring separation of concerns and testability.

## Architecture Layers

```

                   Presentation Layer                     
        
    Activities      Fragments        Adapters     
        
                                                       
    
                ViewModels (LiveData)                 
    

                          

                    Domain Layer                          
        
    Use Cases         Models       Repositories   
                                   (Interfaces)   
        

                          

                     Data Layer                           
        
   API Clients     Room Database   Data Sources   
        
    
           Repository Implementations                 
    

```

## Layer Details

### . Presentation Layer (UI)

#### Components
- **Activities**: Container for fragments, manages navigation
- **Fragments**: Display UI and observe ViewModels
- **ViewModels**: Hold UI state, execute business logic via use cases
- **Adapters**: Bind data to RecyclerViews

#### Responsibilities
- Display data to user
- Handle user interactions
- Observe LiveData/StateFlow from ViewModels
- Navigation between screens

#### Example: AnalysisFragment
```kotlin
@AndroidEntryPoint
class AnalysisFragment : Fragment() {
    private val viewModel: AnalysisViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupFilters()
        viewModel.loadCryptoAssets()
    }
    
    private fun setupObservers() {
        viewModel.patterns.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> displayPatterns(resource.data)
                is Resource.Error -> showError(resource.message)
                is Resource.Loading -> showLoading()
            }
        }
    }
}
```

### . Domain Layer (Business Logic)

#### Components
- **Use Cases**: Single-purpose business logic operations
- **Domain Models**: Pure business entities
- **Repository Interfaces**: Contracts for data access

#### Responsibilities
- Business rules and validation
- Coordinate data from multiple sources
- Transform data to domain models
- Independent of frameworks

#### Example: Pattern Detection Use Case
```kotlin
class DetectPatternsUseCase @Inject constructor(
    private val repository: CryptoRepository,
    private val patternAnalyzer: PatternAnalyzer
) {
    suspend operator fun invoke(
        symbol: String,
        filters: PatternFilter
    ): Result<List<Pattern>> {
        return try {
            val priceData = repository.getPriceHistory(symbol)
            val patterns = patternAnalyzer.analyze(priceData, filters)
            Result.success(patterns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### . Data Layer

#### Components
- **API Clients**: Retrofit interfaces for remote data
- **DAOs**: Room database access objects
- **Data Sources**: Remote and local data source implementations
- **Repository Implementations**: Combine data sources

#### Responsibilities
- Fetch data from APIs
- Cache data locally
- Provide data to domain layer
- Handle data synchronization

#### Example: Repository Implementation
```kotlin
class CryptoRepositoryImpl @Inject constructor(
    private val binanceApi: BinanceApi,
    private val coinGeckoApi: CoinGeckoApi,
    private val priceDao: PriceDataDao,
    private val dispatcher: CoroutineDispatcher
) : CryptoRepository {
    
    override suspend fun getPriceHistory(
        symbol: String
    ): List<PriceData> = withContext(dispatcher) {
        // Try local cache first
        val cached = priceDao.getPriceData(symbol)
        if (cached.isNotEmpty() && !isStale(cached)) {
            return@withContext cached
        }
        
        // Fetch from API
        val fresh = fetchFromApi(symbol)
        priceDao.insertAll(fresh)
        fresh
    }
}
```

## Pattern Detection System

### Analysis Pipeline

```
Raw Price Data → Data Preprocessing → Feature Extraction → Pattern Detection → Confidence Scoring → Results
```

#### . Data Preprocessing
- Remove outliers
- Fill missing values
- Normalize price data
- Calculate indicators (MA, RSI, etc.)

#### . Feature Extraction
- Time-based features (hour, day, week, month)
- Price movement features (% change, volatility)
- Volume features
- Technical indicators

#### . Pattern Detection Algorithms

##### Time-Based Patterns
```kotlin
class TimeBasedPatternDetector {
    fun detectDailyPatterns(data: List<PriceData>): List<Pattern> {
        val groupedByDay = data.groupBy { 
            getDayOfWeek(it.timestamp) 
        }
        
        return groupedByDay.map { (day, prices) ->
            analyzeGroupForPatterns(day, prices)
        }.flatten()
    }
}
```

##### Technical Patterns
```kotlin
class TechnicalPatternDetector {
    fun detectMovingAverageCross(
        data: List<PriceData>,
        shortPeriod: Int = 7,
        longPeriod: Int = 0
    ): List<Pattern> {
        val shortMA = calculateMA(data, shortPeriod)
        val longMA = calculateMA(data, longPeriod)
        
        return findCrossoverPoints(shortMA, longMA)
    }
}
```

#### 4. Confidence Scoring
```kotlin
class PatternConfidenceCalculator {
    fun calculateConfidence(pattern: Pattern): Double {
        val factors = listOf(
            frequencyScore(pattern.frequency),
            consistencyScore(pattern.occurrences),
            recencyScore(pattern.lastOccurrence),
            significanceScore(pattern.averageReturn)
        )
        
        return factors.average()
    }
}
```

## Dependency Injection (Hilt)

### Module Structure

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "soothsayer.db"
        ).build()
    }
    
    @Provides
    fun providePatternAnalyzer(): PatternAnalyzer {
        return PatternAnalyzerImpl()
    }
}
```

## Database Schema

### Room Database

```kotlin
@Database(
    entities = [
        CryptoAsset::class,
        PriceData::class,
        Pattern::class
    ],
    version = 
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cryptoAssetDao(): CryptoAssetDao
    abstract fun priceDataDao(): PriceDataDao
    abstract fun patternDao(): PatternDao
}
```

### DAOs

```kotlin
@Dao
interface PriceDataDao {
    @Query("SELECT * FROM price_data WHERE symbol = :symbol ORDER BY timestamp DESC")
    suspend fun getPriceData(symbol: String): List<PriceData>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(priceData: List<PriceData>)
    
    @Query("DELETE FROM price_data WHERE timestamp < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Long)
}
```

## State Management

### ViewModel State
```kotlin
data class AnalysisUiState(
    val selectedAsset: CryptoAsset? = null,
    val filters: PatternFilter = PatternFilter(),
    val patterns: List<Pattern> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### StateFlow Usage
```kotlin
class AnalysisViewModel @Inject constructor(
    private val detectPatternsUseCase: DetectPatternsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()
    
    fun analyzePatterns() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            detectPatternsUseCase(
                symbol = uiState.value.selectedAsset?.symbol ?: return@launch,
                filters = uiState.value.filters
            ).onSuccess { patterns ->
                _uiState.update { 
                    it.copy(patterns = patterns, isLoading = false) 
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(error = error.message, isLoading = false) 
                }
            }
        }
    }
}
```

## Background Processing

### WorkManager for Data Sync

```kotlin
class DataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            syncPriceData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

## Testing Strategy

### Unit Tests
- ViewModels (with test coroutines)
- Use Cases
- Pattern detection algorithms
- Data transformations

### Integration Tests
- Repository with local database
- API client responses
- End-to-end pattern detection

### UI Tests
- Fragment navigation
- User interactions
- Data display

## Performance Considerations

### Optimization Strategies
. **Pagination**: Load price data in chunks
. **Caching**: Multi-level caching (memory, disk, network)
. **Background Processing**: Heavy calculations in background threads
4. **Lazy Loading**: Load patterns on demand
5. **Database Indexing**: Index frequently queried columns

### Memory Management
- Use `Flow` for large data streams
- Clear old cache periodically
- Limit in-memory data retention

## Security

### Data Protection
- Encrypt sensitive local data
- Secure API key storage
- HTTPS only communication
- Certificate pinning

### Privacy
- No personal data collection
- Local-first data storage
- Optional cloud backup

## Future Enhancements

### Planned Features
. Machine learning pattern prediction
. Real-time notifications
. Portfolio tracking
4. Social sentiment integration
5. Advanced backtesting
6. Cloud synchronization
7. Multi-device support

### Scalability Considerations
- Modular architecture for easy feature addition
- Plugin system for custom pattern detectors
- API versioning for backward compatibility
