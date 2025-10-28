package com.soothsayer.predictor.ui.fragments

import android.graphics.Color
import android.os.Bundle
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
import com.github.mikephil.charting.formatter.ValueFormatter
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
    private var currentSymbol: String = "BTCUSDT"
    private val filterStates = mutableMapOf<String, Boolean>()
    
    companion object {
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
        
        // Set current selection (works for both initial load and restoration)
        val currentCrypto = CryptoList.getDisplayName(currentSymbol) ?: cryptoNames[0]
        binding.cryptoAutocomplete.setText(currentCrypto, false)
        
        // Handle selection
        binding.cryptoAutocomplete.setOnItemClickListener { _, _, position, _ ->
            val selectedDisplayName = cryptoNames[position]
            val apiSymbol = CryptoList.getApiSymbol(selectedDisplayName)
            
            if (apiSymbol != null && apiSymbol != currentSymbol) {
                currentSymbol = apiSymbol
                viewModel.analyzePatterns(currentSymbol)
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
                viewModel.analyzePatterns(currentSymbol)
            }
        }
    }
    
    private fun setupChart() {
        binding.priceChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
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
            legend.textColor = Color.WHITE
            setExtraOffsets(10f, 10f, 10f, 25f) // Extra padding for rotated labels
            
            // Click to open fullscreen chart
            setOnClickListener {
                if (currentPriceData.isNotEmpty()) {
                    ChartFullscreenDialog.newInstance(currentPriceData, currentSymbol)
                        .show(childFragmentManager, "ChartFullscreenDialog")
                }
            }
        }
    }
    
    private fun updateChart(priceData: List<com.soothsayer.predictor.data.models.PriceData>) {
        if (priceData.isEmpty()) {
            binding.priceChart.clear()
            return
        }
        
        // Store current price data for fullscreen view and state restoration
        currentPriceData = ArrayList(priceData)
        
        // Set marker view for tooltips
        binding.priceChart.marker = PriceMarkerView(requireContext(), priceData)
        
        // Use index for X-axis (0, 1, 2...) instead of timestamp
        val entries = priceData.mapIndexed { index, data -> 
            Entry(index.toFloat(), data.close.toFloat()) 
        }
        
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
        
        binding.priceChart.data = LineData(dataSet)
        
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
        
        binding.priceChart.invalidate()
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.patterns.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        resource.data?.let { patterns ->
                            patternAdapter.submitList(patterns)
                        }
                    }
                    is Resource.Error -> {
                        hideLoading()
                        showError(resource.message ?: "Unknown error")
                    }
                }
            }
        }
        
        // Observe price data for chart
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.priceData.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { updateChart(it) }
                    }
                    else -> {
                        // Chart remains empty or shows error
                    }
                }
            }
        }
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
        _binding = null
    }
}
