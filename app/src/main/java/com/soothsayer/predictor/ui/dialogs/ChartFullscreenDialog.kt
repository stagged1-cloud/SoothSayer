package com.soothsayer.predictor.ui.dialogs

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PriceData
import com.soothsayer.predictor.databinding.DialogChartFullscreenBinding
import com.soothsayer.predictor.ui.views.PriceMarkerView
import com.soothsayer.predictor.utils.CryptoColorMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartFullscreenDialog : DialogFragment() {
    
    private var _binding: DialogChartFullscreenBinding? = null
    private val binding get() = _binding!!
    
    private var priceData: ArrayList<PriceData> = ArrayList()
    private var patterns: ArrayList<Pattern> = ArrayList()
    private var symbol: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        
        // Restore state if available
        arguments?.let {
            priceData = it.getParcelableArrayList(ARG_PRICE_DATA) ?: ArrayList()
            patterns = it.getParcelableArrayList(ARG_PATTERNS) ?: ArrayList()
            symbol = it.getString(ARG_SYMBOL, "")
        }
        
        savedInstanceState?.let {
            symbol = it.getString(KEY_SYMBOL, symbol)
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChartFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        
        binding.symbolText.text = symbol
        
        setupChart()
        updateChart()
    }
    
    private fun setupChart() {
        binding.fullscreenChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            isDoubleTapToZoomEnabled = true
            
            // Set marker view for tooltips
            marker = PriceMarkerView(requireContext(), priceData, patterns)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                textColor = Color.WHITE
                gridColor = Color.argb(50, 255, 255, 255)
                granularity = 1f
                setLabelCount(5, false)
                labelRotationAngle = -45f
                setAvoidFirstLastClipping(true)
                isGranularityEnabled = true
                textSize = 11f
            }
            
            axisLeft.apply {
                textColor = Color.WHITE
                setDrawGridLines(true)
                gridColor = Color.argb(50, 255, 255, 255)
                setLabelCount(8, false)
                textSize = 10f
            }
            
            axisRight.isEnabled = false
            legend.apply {
                textColor = Color.WHITE
                textSize = 12f
            }
            setExtraOffsets(10f, 10f, 10f, 35f)
        }
    }
    
    private fun updateChart() {
        if (priceData.isEmpty()) {
            return
        }
        
        val entries = priceData.mapIndexed { index, data ->
            Entry(index.toFloat(), data.close.toFloat())
        }
        
        // Get color based on crypto symbol
        val chartColor = CryptoColorMapper.getColorForSymbol(symbol)
        val highlightColor = Color.parseColor("#03DAC5")
        
        val dataSet = LineDataSet(entries, "$symbol Price").apply {
            color = chartColor
            setDrawCircles(true)
            circleRadius = 2f
            setCircleColor(chartColor)
            lineWidth = 2f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = chartColor
            fillAlpha = 50
            highLightColor = highlightColor
            highlightLineWidth = 1.5f
            enableDashedHighlightLine(10f, 5f, 0f)
        }
        
        // Create pattern markers if patterns are available
        val dataSets = mutableListOf<ILineDataSet>(dataSet)
        
        if (patterns.isNotEmpty()) {
            val markerData = createPatternMarkerEntries(priceData, patterns)
            
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
                
                val markerDataSet = LineDataSet(markerEntries, label).apply {
                    setDrawCircles(true)
                    setCircleColor(color)
                    circleRadius = 8f
                    circleHoleRadius = 4f
                    circleHoleColor = Color.WHITE
                    setDrawValues(false)
                    lineWidth = 0f
                    setDrawHighlightIndicators(false)
                }
                dataSets.add(markerDataSet)
            }
        }
        
        binding.fullscreenChart.data = LineData(dataSets)
        binding.fullscreenChart.legend.isEnabled = patterns.isNotEmpty()
        
        binding.fullscreenChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < priceData.size) {
                    dateFormat.format(Date(priceData[index].timestamp))
                } else {
                    ""
                }
            }
        }
        
        binding.fullscreenChart.invalidate()
        binding.fullscreenChart.animateX(800)
    }
    
    private fun createPatternMarkerEntries(
        priceData: List<PriceData>,
        patterns: List<Pattern>
    ): List<Pair<Entry, Int>> {
        if (patterns.isEmpty() || priceData.isEmpty()) return emptyList()
        
        val markerEntries = mutableListOf<Pair<Entry, Int>>()
        
        patterns.forEach { pattern ->
            val closestIndex = priceData.indexOfFirst { 
                it.timestamp >= pattern.lastOccurrence 
            }
            
            if (closestIndex >= 0 && closestIndex < priceData.size) {
                val pricePoint = priceData[closestIndex]
                val entry = Entry(closestIndex.toFloat(), pricePoint.close.toFloat())
                val color = getPatternMarkerColor(pattern.patternType)
                markerEntries.add(Pair(entry, color))
            }
        }
        
        return markerEntries
    }
    
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
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Redraw chart on orientation change
        setupChart()
        updateChart()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SYMBOL, symbol)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val KEY_SYMBOL = "symbol"
        private const val ARG_PRICE_DATA = "price_data"
        private const val ARG_PATTERNS = "patterns"
        private const val ARG_SYMBOL = "symbol"
        
        fun newInstance(priceData: List<PriceData>, symbol: String, patterns: List<Pattern> = emptyList()): ChartFullscreenDialog {
            return ChartFullscreenDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PRICE_DATA, ArrayList(priceData))
                    putParcelableArrayList(ARG_PATTERNS, ArrayList(patterns))
                    putString(ARG_SYMBOL, symbol)
                }
            }
        }
    }
}
