package com.soothsayer.predictor.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.PriceData
import com.soothsayer.predictor.data.models.Resource
import com.soothsayer.predictor.databinding.FragmentAnalysisBinding
import com.soothsayer.predictor.ui.adapters.PatternAdapter
import com.soothsayer.predictor.ui.dialogs.ChartFullscreenDialog
import com.soothsayer.predictor.ui.viewmodels.AnalysisViewModel
import com.soothsayer.predictor.ui.views.PriceMarkerView
import com.soothsayer.predictor.utils.CryptoColorMapper
import com.soothsayer.predictor.utils.CryptoList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main analysis fragment for pattern detection and visualization
 */
@AndroidEntryPoint
class AnalysisFragment : Fragment() {
    
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnalysisViewModel by viewModels()
    private lateinit var patternAdapter: PatternAdapter
    
    private var currentPriceData: ArrayList<PriceData> = ArrayList()
    private var currentPatterns: List<com.soothsayer.predictor.data.models.Pattern> = emptyList()
    private var currentSymbol: String = "BTCUSDT"
    private val filterStates = mutableMapOf<String, Boolean>()
    
    companion object {
        private const val TAG = "AnalysisFragment"
        private const val KEY_CURRENT_SYMBOL = "current_symbol"
        private const val KEY_PRICE_DATA = "price_data"
        private const val KEY_FILTER_STATES = "filter_states"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore saved state if available
        savedInstanceState?.let {
            currentSymbol = it.getString(KEY_CURRENT_SYMBOL, "BTCUSDT")
            currentPriceData = it.getParcelableArrayList(KEY_PRICE_DATA) ?: ArrayList()
            
            // Restore filter states
            @Suppress("DEPRECATION")
            val savedFilters = it.getSerializable(KEY_FILTER_STATES) as? HashMap<String, Boolean>
            savedFilters?.let { filters ->
                filterStates.putAll(filters)
            }
        }
        
        setupRecyclerView()
        setupCryptoSpinner()
        setupFilterSwitches()
        setupAnalyzeButton()
        setupChart()
        observeViewModel()
        
        // Restore chart if we have data
        if (currentPriceData.isNotEmpty()) {
            updateChart(currentPriceData)
        }
        
        // Load initial data
        viewModel.loadCryptoAssets()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_SYMBOL, currentSymbol)
        outState.putParcelableArrayList(KEY_PRICE_DATA, currentPriceData)
        
        // Save filter states - check if binding is not null
        _binding?.let { binding ->
            val filtersContainer = binding.filtersContainer
            for (i in 0 until filtersContainer.childCount) {
                val child = filtersContainer.getChildAt(i)
                if (child is SwitchMaterial) {
                    val key = child.tag as? String
                    if (key != null) {
                        filterStates[key] = child.isChecked
                    }
                }
            }
        }
        outState.putSerializable(KEY_FILTER_STATES, HashMap(filterStates))
    }
    
    private fun setupRecyclerView() {
        patternAdapter = PatternAdapter { pattern ->
            // Navigate to pattern detail when clicked
            val action = AnalysisFragmentDirections.actionAnalysisToPatternDetail(pattern)
            findNavController().navigate(action)
        }
        binding.patternsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = patternAdapter
        }
    }
    
    private fun setupCryptoSpinner() {
        val cryptoNames = CryptoList.getDisplayNames()
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cryptoNames
        )
        
        binding.cryptoAutocomplete.setAdapter(adapter)
        binding.cryptoAutocomplete.threshold = 1  // Show suggestions after 1 character
        
        // Always show all options when dropdown is opened - with safety checks
        binding.cryptoAutocomplete.setOnClickListener {
            // Only show dropdown if activity is valid and view is attached to window
            if (activity != null && !requireActivity().isFinishing && !requireActivity().isDestroyed 
                && binding.cryptoAutocomplete.windowToken != null) {
                binding.cryptoAutocomplete.showDropDown()
            }
        }
        
        binding.cryptoAutocomplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Only show dropdown if activity is valid and view is attached to window
                if (activity != null && !requireActivity().isFinishing && !requireActivity().isDestroyed 
                    && binding.cryptoAutocomplete.windowToken != null) {
                    // Post to ensure window is fully ready
                    binding.cryptoAutocomplete.post {
                        if (binding.cryptoAutocomplete.windowToken != null) {
                            binding.cryptoAutocomplete.showDropDown()
                        }
                    }
                }
            }
        }
        
        // Set current selection (works for both initial load and restoration)
        val currentCrypto = CryptoList.getDisplayName(currentSymbol) ?: cryptoNames[0]
        binding.cryptoAutocomplete.setText(currentCrypto, false)
        
        // Handle selection
        binding.cryptoAutocomplete.setOnItemClickListener { _, _, position, _ ->
            val selectedDisplayName = cryptoNames[position]
            val apiSymbol = CryptoList.getApiSymbol(selectedDisplayName)
            
            if (apiSymbol != null && apiSymbol != currentSymbol) {
                currentSymbol = apiSymbol
                // Clear old chart data and cached data
                binding.priceChart.clear()
                currentPriceData.clear()
                currentPatterns = emptyList()
                binding.dataStatus.text = "Loading..."
                // Force refresh data when switching cryptos
                viewModel.analyzePatterns(currentSymbol, forceRefresh = true)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Restore the selected crypto when returning from detail view
        val currentCrypto = CryptoList.getDisplayName(currentSymbol)
        if (currentCrypto != null) {
            binding.cryptoAutocomplete.setText(currentCrypto, false)
        }
        
        // Reset the adapter to show all options again
        val cryptoNames = CryptoList.getDisplayNames()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cryptoNames
        )
        binding.cryptoAutocomplete.setAdapter(adapter)
    }
    
    private fun setupFilterSwitches() {
        val filtersContainer = binding.filtersContainer
        
        // Add filter switches programmatically
        val filters = mapOf(
            getString(R.string.filter_hourly) to "hourly",
            getString(R.string.filter_daily) to "daily",
            getString(R.string.filter_weekly) to "weekly",
            getString(R.string.filter_monthly) to "monthly",
            getString(R.string.filter_yearly) to "yearly",
            getString(R.string.filter_moving_averages) to "movingAverages",
            getString(R.string.filter_volume) to "volume",
            getString(R.string.filter_volatility) to "volatility",
            getString(R.string.filter_support_resistance) to "supportResistance",
            getString(R.string.filter_seasonal) to "seasonal"
        )
        
        filters.forEach { (label, key) ->
            val switch = SwitchMaterial(requireContext()).apply {
                text = label
                tag = key
                // Restore saved state, or use default enabled filters
                isChecked = if (filterStates.containsKey(key)) {
                    filterStates[key] ?: false
                } else {
                    key in listOf("daily", "weekly", "monthly", "movingAverages", "volume", "supportResistance")
                }
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.updateFilter(key, isChecked)
                    filterStates[key] = isChecked
                }
            }
            filtersContainer.addView(switch)
        }
    }
    
    private fun setupAnalyzeButton() {
        binding.analyzeButton.setOnClickListener {
            // Check if at least one filter is selected
            val filtersContainer = binding.filtersContainer
            val hasActiveFilter = (0 until filtersContainer.childCount).any { index ->
                val child = filtersContainer.getChildAt(index)
                child is SwitchMaterial && child.isChecked
            }
            
            if (!hasActiveFilter) {
                // Show message if no filters are selected
                Snackbar.make(
                    binding.root,
                    "Please select at least one pattern filter",
                    Snackbar.LENGTH_LONG
                ).setAction("Enable All") {
                    // Enable all filters when user clicks "Enable All"
                    for (i in 0 until filtersContainer.childCount) {
                        val child = filtersContainer.getChildAt(i)
                        if (child is SwitchMaterial) {
                            child.isChecked = true
                        }
                    }
                }.show()
                return@setOnClickListener
            }
            
            val selectedCrypto = binding.cryptoAutocomplete.text.toString()
            val apiSymbol = CryptoList.getApiSymbol(selectedCrypto)
            if (apiSymbol != null) {
                currentSymbol = apiSymbol
                // Clear cached data to ensure fresh analysis
                currentPriceData.clear()
                currentPatterns = emptyList()
                viewModel.analyzePatterns(currentSymbol, forceRefresh = true)
            }
        }
    }
    
    private fun setupChart() {
        Log.d(TAG, "setupChart called")
        binding.priceChart.apply {
            description.isEnabled = false
            setTouchEnabled(true) // Need touch enabled to detect clicks
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            isHighlightPerTapEnabled = false // Disable tap highlighting
            setNoDataText("Tap 'Analyze Patterns' to load chart")
            setNoDataTextColor(Color.GRAY)
            
            // Intercept all touches and convert to fullscreen open
            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    // Open fullscreen on any tap
                    if (currentPriceData.isNotEmpty()) {
                        Log.d(TAG, "Opening fullscreen chart - priceData: ${currentPriceData.size}, patterns: ${currentPatterns.size}")
                        ChartFullscreenDialog.newInstance(currentPriceData, currentSymbol, currentPatterns)
                            .show(childFragmentManager, "ChartFullscreenDialog")
                    } else {
                        Log.d(TAG, "Cannot open fullscreen - no price data available")
                    }
                }
                true // Consume the event to prevent default chart behavior
            }
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                textColor = Color.WHITE
                granularity = 1f
                setLabelCount(5, false) // Show max 5 labels to prevent overlap
                labelRotationAngle = -45f // Rotate labels for better fit
                setAvoidFirstLastClipping(true)
                isGranularityEnabled = true
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        return dateFormat.format(Date(value.toLong()))
                    }
                }
            }
            
            axisLeft.apply {
                textColor = Color.WHITE
                setDrawGridLines(true)
                gridColor = Color.argb(50, 255, 255, 255)
                setLabelCount(6, false)
            }
            
            axisRight.isEnabled = false
            
            // Configure legend
            legend.apply {
                textColor = Color.WHITE
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.LEFT
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                formSize = 8f
                xEntrySpace = 8f
                yEntrySpace = 2f
                textSize = 8f
                maxSizePercent = 0.5f
            }
            
            setExtraOffsets(10f, 5f, 10f, 25f) // Reduced top padding, legend is vertical now
        }
    }
    
    /**
     * Create scatter chart entries for pattern markers
     * Maps pattern timestamps to chart indices and creates colored dots
     */
    private fun createPatternMarkerEntries(
        priceData: List<com.soothsayer.predictor.data.models.PriceData>,
        patterns: List<com.soothsayer.predictor.data.models.Pattern>
    ): List<Pair<Entry, Int>> {
        if (patterns.isEmpty() || priceData.isEmpty()) return emptyList()
        
        val markerEntries = mutableListOf<Pair<Entry, Int>>()
        
        patterns.forEach { pattern ->
            // Find the closest price data point to this pattern's lastOccurrence
            val closestIndex = priceData.indices.minByOrNull { index ->
                Math.abs(priceData[index].timestamp - pattern.lastOccurrence)
            } ?: -1
            
            if (closestIndex >= 0 && closestIndex < priceData.size) {
                val pricePoint = priceData[closestIndex]
                val entry = Entry(closestIndex.toFloat(), pricePoint.close.toFloat())
                val color = getPatternMarkerColor(pattern.patternType)
                markerEntries.add(Pair(entry, color))
                
                android.util.Log.d("AnalysisFragment", "Pattern ${pattern.patternType} mapped to index $closestIndex (timestamp: ${pricePoint.timestamp})")
            }
        }
        
        return markerEntries
    }
    
    /**
     * Get marker color based on pattern type
     * Red: Bearish patterns (oversold, drops, resistance)
     * Green: Bullish patterns (overbought, gains, breakouts)
     * Yellow: Support levels
     * Blue: Divergence patterns
     */
    private fun getPatternMarkerColor(patternType: com.soothsayer.predictor.data.models.PatternType): Int {
        return when (patternType) {
            // Bearish - Red
            com.soothsayer.predictor.data.models.PatternType.RSI_OVERSOLD,
            com.soothsayer.predictor.data.models.PatternType.PRICE_DROP,
            com.soothsayer.predictor.data.models.PatternType.HOURLY_DROP,
            com.soothsayer.predictor.data.models.PatternType.RESISTANCE_LEVEL,
            com.soothsayer.predictor.data.models.PatternType.CONSECUTIVE_LOSSES,
            com.soothsayer.predictor.data.models.PatternType.RSI_BEARISH_DIVERGENCE -> Color.RED
            
            // Bullish - Green
            com.soothsayer.predictor.data.models.PatternType.RSI_OVERBOUGHT,
            com.soothsayer.predictor.data.models.PatternType.MOVING_AVERAGE_CROSS,
            com.soothsayer.predictor.data.models.PatternType.PRICE_SPIKE,
            com.soothsayer.predictor.data.models.PatternType.HOURLY_SPIKE,
            com.soothsayer.predictor.data.models.PatternType.BREAKOUT,
            com.soothsayer.predictor.data.models.PatternType.CONSECUTIVE_GAINS -> Color.GREEN
            
            // Support - Yellow/Gold
            com.soothsayer.predictor.data.models.PatternType.SUPPORT_LEVEL -> Color.rgb(255, 193, 7)
            
            // Divergence - Blue
            com.soothsayer.predictor.data.models.PatternType.RSI_BULLISH_DIVERGENCE -> Color.BLUE
            
            // Volume - Orange
            com.soothsayer.predictor.data.models.PatternType.VOLUME_SPIKE -> Color.rgb(255, 152, 0)
            
            // Volatility - Purple
            com.soothsayer.predictor.data.models.PatternType.HIGH_VOLATILITY,
            com.soothsayer.predictor.data.models.PatternType.LOW_VOLATILITY -> Color.rgb(156, 39, 176)
            
            // Default - White for unspecified patterns
            else -> Color.WHITE
        }
    }
    
    private fun updateChart(priceData: List<com.soothsayer.predictor.data.models.PriceData>) {
        android.util.Log.d("AnalysisFragment", "updateChart called - symbol: $currentSymbol, priceData: ${priceData.size}, patterns: ${currentPatterns.size}")
        
        if (priceData.isEmpty()) {
            android.util.Log.w("AnalysisFragment", "updateChart called with empty price data for $currentSymbol")
            binding.priceChart.clear()
            binding.priceChart.invalidate()
            return
        }
        
        // TEMP: Add dummy patterns for Ocean testing
        val patternsToUse = if (currentSymbol == "OCEANUSDT" && currentPatterns.isEmpty()) {
            // Let the pattern analyzer run on the dummy data instead of hardcoded patterns
            currentPatterns
        } else {
            currentPatterns
        }
        
        try {
            // Store current price data for fullscreen view and state restoration
            currentPriceData = ArrayList(priceData)
            
            // Use index for X-axis (0, 1, 2...) instead of timestamp
            val entries = priceData.mapIndexed { index, data -> 
                Entry(index.toFloat(), data.close.toFloat()) 
            }
            android.util.Log.d("AnalysisFragment", "Created ${entries.size} price entries")
            
            // Get color based on current crypto symbol
            val chartColor = CryptoColorMapper.getColorForSymbol(currentSymbol)
            val fillColor = CryptoColorMapper.getLightColorForSymbol(currentSymbol)
            
            val dataSet = LineDataSet(entries, "$currentSymbol Price").apply {
                color = chartColor
                setDrawCircles(false)
                lineWidth = 2f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                this.fillColor = chartColor
                fillAlpha = 50
            }
            
            // Create pattern markers if patterns are available
            val dataSets = mutableListOf<ILineDataSet>(dataSet)
            
            if (patternsToUse.isNotEmpty()) {
                val markerData = createPatternMarkerEntries(priceData, patternsToUse)
                
                android.util.Log.d("AnalysisFragment", "Patterns detected: ${patternsToUse.size}")
                android.util.Log.d("AnalysisFragment", "Marker entries created: ${markerData.size}")
                
                // Group markers by color and create descriptive labels
                val markersByColor = markerData.groupBy { it.second }
                val colorLabels = mapOf(
                    Color.RED to "Bearish Signals",
                    Color.GREEN to "Bullish Signals",
                    Color.rgb(255, 193, 7) to "Support Levels",
                    Color.BLUE to "Divergence",
                    Color.rgb(255, 152, 0) to "Volume Spikes",
                    Color.rgb(156, 39, 176) to "Volatility"
            )
            
            markersByColor.forEach { (color, markers) ->
                val markerEntries = markers.map { it.first }
                val label = colorLabels[color] ?: "Pattern"
                
                android.util.Log.d("AnalysisFragment", "Creating ${label} dataset with ${markerEntries.size} markers")
                
                val markerDataSet = LineDataSet(markerEntries, label).apply {
                    this.color = color // For legend color
                    setDrawCircles(true)
                    setCircleColor(color)
                    circleRadius = 5f // Match legend size
                    circleHoleRadius = 0f // No hole, solid circles
                    setDrawValues(false)
                    lineWidth = 0f // No lines between markers
                    setDrawHighlightIndicators(false)
                    isHighlightEnabled = true
                }
                dataSets.add(markerDataSet)
                android.util.Log.d("AnalysisFragment", "Added marker dataset: ${colorLabels[color]} with ${markers.size} markers")
            }
        } else {
            android.util.Log.d("AnalysisFragment", "No patterns to display on chart")
        }
        
        binding.priceChart.data = LineData(dataSets)
        binding.priceChart.legend.isEnabled = dataSets.isNotEmpty()
        
        // Update X-axis formatter to show dates correctly based on index
        binding.priceChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < priceData.size) {
                    dateFormat.format(Date(priceData[index].timestamp))
                } else {
                    ""
                }
            }
        }
        
        android.util.Log.d("AnalysisFragment", "Chart data set with ${dataSets.size} datasets")
        // Simplified update - just set data and invalidate
        binding.priceChart.invalidate()
        android.util.Log.d("AnalysisFragment", "Chart updated successfully")
        } catch (e: Exception) {
            android.util.Log.e("AnalysisFragment", "Error updating chart", e)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.patterns.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "Patterns loading...")
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        resource.data?.let { patterns ->
                            Log.d(TAG, "Patterns loaded successfully: ${patterns.size} patterns")
                            currentPatterns = patterns
                            patternAdapter.submitList(patterns)
                            // Update chart with pattern markers if price data is available
                            if (currentPriceData.isNotEmpty()) {
                                Log.d(TAG, "Updating chart with patterns")
                                updateChart(currentPriceData)
                            } else {
                                Log.w(TAG, "Price data not available yet for pattern markers")
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideLoading()
                        val errorMsg = resource.message ?: "Unknown error"
                        Log.e(TAG, "Patterns error: $errorMsg")
                        showError(errorMsg)
                    }
                }
            }
        }
        
        // Observe price data for chart
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.priceData.collect { resource ->
                android.util.Log.d("AnalysisFragment", "Price data resource for $currentSymbol: ${resource.javaClass.simpleName}")
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            android.util.Log.d("AnalysisFragment", "Price data received: ${data.size} points for $currentSymbol")
                            if (data.isNotEmpty()) {
                                updateChart(data)
                                updateDataStatus(data)
                            } else {
                                android.util.Log.w("AnalysisFragment", "Empty price data for $currentSymbol")
                                binding.dataStatus.text = "No data available for $currentSymbol"
                                binding.priceChart.clear()
                                binding.priceChart.invalidate()
                            }
                        } ?: run {
                            android.util.Log.w("AnalysisFragment", "Null price data for $currentSymbol")
                            binding.dataStatus.text = "No data available"
                            binding.priceChart.clear()
                            binding.priceChart.invalidate()
                        }
                    }
                    is Resource.Loading -> {
                        android.util.Log.d("AnalysisFragment", "Loading price data for $currentSymbol")
                        binding.dataStatus.text = "Loading..."
                        binding.priceChart.clear()
                        binding.priceChart.invalidate()
                    }
                    is Resource.Error -> {
                        android.util.Log.e("AnalysisFragment", "Price data error for $currentSymbol: ${resource.message}")
                        binding.dataStatus.text = "Error: ${resource.message}"
                        binding.priceChart.clear()
                        binding.priceChart.invalidate()
                    }
                }
            }
        }
    }
    
    private fun updateDataStatus(priceData: List<PriceData>) {
        if (priceData.isEmpty()) {
            binding.dataStatus.text = "No data"
            return
        }
        
        // Get the most recent data timestamp
        val latestTimestamp = priceData.maxOfOrNull { it.timestamp } ?: return
        val now = System.currentTimeMillis()
        val diffMinutes = (now - latestTimestamp) / (1000 * 60)
        
        val statusText = when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "${diffMinutes}m ago"
            diffMinutes < 1440 -> "${diffMinutes / 60}h ago"
            else -> "${diffMinutes / 1440}d ago"
        }
        
        binding.dataStatus.text = statusText
    }
    
    private fun showLoading() {
        binding.analyzeButton.isEnabled = false
        binding.analyzeButton.text = getString(R.string.loading)
    }
    
    private fun hideLoading() {
        binding.analyzeButton.isEnabled = true
        binding.analyzeButton.text = getString(R.string.analyze_patterns)
    }
    
    private fun showError(@Suppress("UNUSED_PARAMETER") message: String) {
        // TODO: Show error snackbar or dialog
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear chart data to free memory
        binding.priceChart.clear()
        binding.priceChart.data = null
        currentPriceData.clear()
        _binding = null
    }
}
