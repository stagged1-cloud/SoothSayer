package com.soothsayer.predictor.ui.dialogs

import android.app.Dialog
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
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.databinding.DialogPatternChartFullscreenBinding
import com.soothsayer.predictor.ui.views.PatternMarkerView
import com.soothsayer.predictor.utils.CryptoColorMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PatternChartFullscreenDialog : DialogFragment() {
    
    private var _binding: DialogPatternChartFullscreenBinding? = null
    private val binding get() = _binding!!
    
    private var pattern: Pattern? = null
    private val timestamps = mutableListOf<Long>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        
        arguments?.let {
            pattern = it.getParcelable(ARG_PATTERN)
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
        _binding = DialogPatternChartFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore timestamps if available (for rotation)
        savedInstanceState?.getLongArray(KEY_TIMESTAMPS)?.let { savedTimestamps ->
            timestamps.clear()
            timestamps.addAll(savedTimestamps.toList())
        }
        
        pattern?.let { p ->
            binding.closeButton.setOnClickListener {
                dismiss()
            }
            
            binding.patternTitle.text = "${p.symbol} - ${formatPatternType(p.patternType)}"
            binding.patternInfo.text = "Frequency: ${p.frequency} occurrences • Confidence: ${(p.confidence * 100).toInt()}%"
            
            setupChart(p)
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save timestamps for rotation
        if (timestamps.isNotEmpty()) {
            outState.putLongArray(KEY_TIMESTAMPS, timestamps.toLongArray())
        }
    }
    
    private fun setupChart(pattern: Pattern) {
        // Generate occurrence data
        val entries = mutableListOf<Entry>()
        
        // Only regenerate timestamps if not already populated (e.g., from savedInstanceState)
        if (timestamps.isEmpty()) {
            val currentTime = System.currentTimeMillis()
            
            val averageInterval = if (pattern.frequency > 1) {
                (currentTime - pattern.lastOccurrence) / (pattern.frequency - 1)
            } else {
                TimeUnit.DAYS.toMillis(7)
            }
            
            // Use more data points for fullscreen view - ensure minimum 2 points
            val pointsToShow = maxOf(2, minOf(pattern.frequency, 20))
            for (i in 0 until pointsToShow) {
                val timestamp = pattern.lastOccurrence - (averageInterval * i)
                timestamps.add(0, timestamp)
            }
            
            if (timestamps.size < 2) {
                // Add dummy data if we don't have enough points
                timestamps.clear()
                val now = currentTime
                timestamps.add(now - TimeUnit.DAYS.toMillis(7))
                timestamps.add(now)
            }
        }
        
        // Build entries from timestamps
        for (i in timestamps.indices) {
            entries.add(Entry(i.toFloat(), (i + 1).toFloat()))
        }
        
        val chartColor = CryptoColorMapper.getColorForSymbol(pattern.symbol)
        
        val dataSet = LineDataSet(entries, "Pattern Occurrences Over Time").apply {
            color = chartColor
            setCircleColor(chartColor)
            circleRadius = 3f
            lineWidth = 2.5f
            setDrawValues(false) // Disable value labels to prevent crashes
            valueTextColor = Color.WHITE
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = chartColor
            fillAlpha = 30
            highLightColor = Color.parseColor("#03DAC5")
            highlightLineWidth = 1.5f
            enableDashedHighlightLine(10f, 5f, 0f)
        }
        
        binding.fullscreenChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            // Completely disable value drawing to prevent crash
            data?.setDrawValues(false)
            
            // Add marker view for tooltips AFTER timestamps are populated
            if (timestamps.isNotEmpty()) {
                marker = PatternMarkerView(requireContext(), timestamps)
            }
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                textColor = Color.WHITE
                gridColor = Color.argb(50, 255, 255, 255)
                granularity = 1f
                setLabelCount(8, false)
                labelRotationAngle = -45f
                setAvoidFirstLastClipping(true)
                isGranularityEnabled = true
                textSize = 11f
                
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < timestamps.size) {
                            dateFormat.format(Date(timestamps[index]))
                        } else {
                            ""
                        }
                    }
                }
            }
            
            axisLeft.apply {
                textColor = Color.WHITE
                setDrawGridLines(true)
                gridColor = Color.argb(50, 255, 255, 255)
                setLabelCount(8, false)
                textSize = 11f
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            
            legend.apply {
                textColor = Color.WHITE
                textSize = 12f
            }
            
            setExtraOffsets(10f, 10f, 10f, 35f)
            invalidate()
            animateX(800)
        }
    }
    
    private fun formatPatternType(type: com.soothsayer.predictor.data.models.PatternType): String {
        return type.name.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_PATTERN = "pattern"
        private const val KEY_TIMESTAMPS = "timestamps"
        
        fun newInstance(pattern: Pattern): PatternChartFullscreenDialog {
            return PatternChartFullscreenDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATTERN, pattern)
                }
            }
        }
    }
}
