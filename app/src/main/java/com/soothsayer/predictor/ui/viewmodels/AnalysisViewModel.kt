package com.soothsayer.predictor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soothsayer.predictor.data.models.CryptoAsset
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PatternFilter
import com.soothsayer.predictor.data.models.PriceData
import com.soothsayer.predictor.data.models.Resource
import com.soothsayer.predictor.domain.usecases.DetectPatternsUseCase
import com.soothsayer.predictor.domain.usecases.LoadCryptoAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Analysis screen
 * Manages UI state and coordinates use cases
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val detectPatternsUseCase: DetectPatternsUseCase,
    private val loadCryptoAssetsUseCase: LoadCryptoAssetsUseCase
) : ViewModel() {
    
    private val _patterns = MutableStateFlow<Resource<List<Pattern>>>(Resource.Success(emptyList()))
    val patterns: StateFlow<Resource<List<Pattern>>> = _patterns.asStateFlow()
    
    private val _cryptoAssets = MutableStateFlow<Resource<List<CryptoAsset>>>(Resource.Success(emptyList()))
    val cryptoAssets: StateFlow<Resource<List<CryptoAsset>>> = _cryptoAssets.asStateFlow()
    
    private val _priceData = MutableStateFlow<Resource<List<PriceData>>>(Resource.Success(emptyList()))
    val priceData: StateFlow<Resource<List<PriceData>>> = _priceData.asStateFlow()
    
    private var currentFilters = PatternFilter(
        enableDailyAnalysis = true,
        enableWeeklyAnalysis = true,
        enableMonthlyAnalysis = true,
        enableMovingAverages = true,
        enableVolumeCorrelation = true,
        enableSupportResistance = true,
        minimumConfidence = 0.6,
        minimumFrequency = 3
    )
    
    /**
     * Load available crypto assets
     */
    fun loadCryptoAssets() {
        viewModelScope.launch {
            _cryptoAssets.value = Resource.Loading()
            _cryptoAssets.value = loadCryptoAssetsUseCase()
        }
    }
    
    /**
     * Update filter settings
     */
    fun updateFilter(filterKey: String, enabled: Boolean) {
        currentFilters = when (filterKey) {
            "hourly" -> currentFilters.copy(enableHourlyAnalysis = enabled)
            "daily" -> currentFilters.copy(enableDailyAnalysis = enabled)
            "weekly" -> currentFilters.copy(enableWeeklyAnalysis = enabled)
            "monthly" -> currentFilters.copy(enableMonthlyAnalysis = enabled)
            "yearly" -> currentFilters.copy(enableYearlyAnalysis = enabled)
            "movingAverages" -> currentFilters.copy(enableMovingAverages = enabled)
            "volume" -> currentFilters.copy(enableVolumeCorrelation = enabled)
            "volatility" -> currentFilters.copy(enableVolatilityAnalysis = enabled)
            "supportResistance" -> currentFilters.copy(enableSupportResistance = enabled)
            "seasonal" -> currentFilters.copy(enableSeasonalTrends = enabled)
            else -> currentFilters
        }
    }
    
    /**
     * Analyze patterns for selected symbol
     * Uses ALL enabled pattern detection algorithms
     */
    fun analyzePatterns(symbol: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("AnalysisViewModel", "Starting analysis for $symbol")
                _patterns.value = Resource.Loading()
                _priceData.value = Resource.Loading()
                
                // Execute comprehensive pattern detection
                val result = detectPatternsUseCase(
                    symbol = symbol,
                    filters = currentFilters,
                    days = 365 // Analyze 1 year of data
                )
                
                android.util.Log.d("AnalysisViewModel", "Analysis result: $result")
                _patterns.value = result
                
                // Also fetch and expose price data for chart
                if (result is Resource.Success) {
                    val priceDataResult = detectPatternsUseCase.getPriceData(symbol, 90) // Last 90 days for chart
                    android.util.Log.d("AnalysisViewModel", "Price data result: $priceDataResult")
                    _priceData.value = priceDataResult
                }
            } catch (e: Exception) {
                android.util.Log.e("AnalysisViewModel", "Error in analyzePatterns", e)
                _patterns.value = Resource.Error(e.message ?: "Unknown error")
                _priceData.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Load cached patterns without re-analyzing
     */
    fun loadCachedPatterns(symbol: String) {
        viewModelScope.launch {
            _patterns.value = Resource.Loading()
            _patterns.value = detectPatternsUseCase.getCachedPatterns(symbol)
        }
    }
}
