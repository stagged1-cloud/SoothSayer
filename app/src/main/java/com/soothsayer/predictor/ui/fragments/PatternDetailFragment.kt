package com.soothsayer.predictor.ui.fragments

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PatternType
import com.soothsayer.predictor.databinding.FragmentPatternDetailBinding
import com.soothsayer.predictor.ui.dialogs.PatternChartFullscreenDialog
import com.soothsayer.predictor.ui.views.PatternMarkerView
import com.soothsayer.predictor.utils.CryptoColorMapper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Detailed view of a detected pattern with statistics and predictions
 */
@AndroidEntryPoint
class PatternDetailFragment : Fragment() {
    
    private var _binding: FragmentPatternDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: PatternDetailFragmentArgs by navArgs()
    private val timestamps = mutableListOf<Long>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatternDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore timestamps if available (for rotation)
        savedInstanceState?.getLongArray(KEY_TIMESTAMPS)?.let { savedTimestamps ->
            timestamps.clear()
            timestamps.addAll(savedTimestamps.toList())
        }
        
        val pattern = args.pattern
        
        setupToolbar()
        displayPatternDetails(pattern)
        setupMiniChart(pattern)
        animateEntrance()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save timestamps for rotation
        if (timestamps.isNotEmpty()) {
            outState.putLongArray(KEY_TIMESTAMPS, timestamps.toLongArray())
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun displayPatternDetails(pattern: Pattern) {
        binding.apply {
            // Header
            patternTypeTitle.text = formatPatternType(pattern.patternType)
            patternSymbol.text = pattern.symbol
            
            // Confidence
            confidenceValue.text = "${(pattern.confidence * 100).toInt()}%"
            confidenceProgress.progress = (pattern.confidence * 100).toInt()
            
            // Description
            patternDescription.text = pattern.description
            
            // Statistics
            frequencyValue.text = "${pattern.frequency} times"
            lastOccurrenceValue.text = formatDate(pattern.lastOccurrence)
            nextPredictionValue.text = pattern.predictedNextOccurrence?.let {
                formatDate(it)
            } ?: "N/A"
            
            averageReturnValue.text = String.format("%.2f%%", pattern.averageReturnPercentage)
            averageReturnValue.setTextColor(
                if (pattern.averageReturnPercentage >= 0)
                    requireContext().getColor(R.color.success)
                else
                    requireContext().getColor(R.color.error)
            )
            
            // Algorithm info
            algorithmName.text = getAlgorithmName(pattern.patternType)
            algorithmDescription.text = getAlgorithmDescription(pattern.patternType)
            
            // Time analysis
            val daysSinceLastOccurrence = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - pattern.lastOccurrence
            )
            timeSinceLastValue.text = when (daysSinceLastOccurrence) {
                0L -> "Today"
                1L -> "1 day ago"
                else -> "$daysSinceLastOccurrence days ago"
            }
            
            pattern.predictedNextOccurrence?.let {
                val daysUntilNext = TimeUnit.MILLISECONDS.toDays(
                    it - System.currentTimeMillis()
                )
                timeUntilNextValue.text = if (daysUntilNext > 0) {
                    "In $daysUntilNext days"
                } else {
                    "Overdue by ${-daysUntilNext} days"
                }
            } ?: run {
                timeUntilNextValue.text = "N/A"
            }
            
            // Confidence badge color
            val confidenceColor = when {
                pattern.confidence >= 0.8 -> R.color.confidence_high
                pattern.confidence >= 0.6 -> R.color.confidence_medium
                else -> R.color.confidence_low
            }
            confidenceBadge.setCardBackgroundColor(
                requireContext().getColor(confidenceColor)
            )
        }
    }
    
    private fun setupMiniChart(pattern: Pattern) {
        // Generate sample occurrence data for visualization
        val entries = mutableListOf<Entry>()
        
        // Only regenerate timestamps if not already populated (e.g., from savedInstanceState)
        if (timestamps.isEmpty()) {
            val currentTime = System.currentTimeMillis()
            
            // Show last 10 occurrences (simulated based on frequency)
            val averageInterval = if (pattern.frequency > 1) {
                (currentTime - pattern.lastOccurrence) / (pattern.frequency - 1)
            } else {
                TimeUnit.DAYS.toMillis(7)
            }
            
            // Ensure minimum 2 points to avoid negative array size error
            val pointsToShow = maxOf(2, minOf(pattern.frequency, 10))
            for (i in 0 until pointsToShow) {
                val timestamp = pattern.lastOccurrence - (averageInterval * i)
                timestamps.add(0, timestamp)
            }
            
            // Ensure we always have at least 2 entries for the chart
            if (timestamps.size < 2) {
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
        
        if (entries.isNotEmpty()) {
            // Get color based on crypto symbol
            val chartColor = CryptoColorMapper.getColorForSymbol(pattern.symbol)
            
            val dataSet = LineDataSet(entries, "Occurrences").apply {
                color = chartColor
                setCircleColor(chartColor)
                circleRadius = 4f
                lineWidth = 2f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            
            binding.miniChart.apply {
                data = LineData(dataSet)
                description.isEnabled = false
                legend.isEnabled = false
                
                // Completely disable value drawing to prevent crash
                data?.setDrawValues(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.WHITE
                    granularity = 1f
                    setLabelCount(5, false)
                    labelRotationAngle = -45f
                    setAvoidFirstLastClipping(true)
                    isGranularityEnabled = true
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
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
                    gridColor = Color.argb(30, 255, 255, 255)
                    setLabelCount(5, false)
                }
                
                axisRight.isEnabled = false
                setTouchEnabled(true)
                setExtraOffsets(5f, 5f, 5f, 15f)
                
                // Add marker view for tooltips AFTER timestamps are populated
                if (timestamps.isNotEmpty()) {
                    marker = PatternMarkerView(requireContext(), timestamps)
                }
                
                // Click to open fullscreen
                setOnClickListener {
                    PatternChartFullscreenDialog.newInstance(pattern)
                        .show(childFragmentManager, "PatternChartFullscreenDialog")
                }
                
                invalidate()
            }
        }
    }
    
    private fun animateEntrance() {
        // Fade in animation
        binding.root.alpha = 0f
        binding.root.translationY = 50f
        
        binding.root.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()
        
        // Animate confidence progress
        ObjectAnimator.ofInt(
            binding.confidenceProgress,
            "progress",
            0,
            (args.pattern.confidence * 100).toInt()
        ).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }
    
    private fun formatPatternType(type: PatternType): String {
        return type.name
            .split("_")
            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
    }
    
    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    private fun getAlgorithmName(type: PatternType): String {
        return when {
            type.name.contains("HOURLY") -> "Hourly Pattern Detection"
            type.name.contains("DAILY") -> "Daily Pattern Analysis"
            type.name.contains("WEEKLY") -> "Weekly Trend Analysis"
            type.name.contains("MONTHLY") -> "Monthly Cycle Detection"
            type.name.contains("YEARLY") -> "Yearly Seasonal Analysis"
            type.name.contains("MA_CROSS") -> "Moving Average Crossover"
            type.name.contains("VOLUME") -> "Volume Correlation Analysis"
            type.name.contains("VOLATILITY") -> "Volatility Pattern Detection"
            type.name.contains("SUPPORT") || type.name.contains("RESISTANCE") -> "Support/Resistance Level Detection"
            type.name.contains("SEASONAL") -> "Seasonal Trend Analysis"
            type.name.contains("CONSECUTIVE") -> "Consecutive Pattern Recognition"
            type.name.contains("SPIKE") || type.name.contains("DROP") -> "Price Movement Detection"
            else -> "Advanced Pattern Recognition"
        }
    }
    
    private fun getAlgorithmDescription(type: PatternType): String {
        return when {
            type.name.contains("HOURLY") -> "Analyzes price movements at specific hours of the day to identify recurring hourly patterns."
            type.name.contains("DAILY") -> "Examines daily price trends to detect patterns that occur on specific days of the week."
            type.name.contains("WEEKLY") -> "Studies weekly price movements to find patterns that repeat across different weeks."
            type.name.contains("MONTHLY") -> "Tracks monthly price behavior to identify cyclical patterns within calendar months."
            type.name.contains("YEARLY") -> "Analyzes long-term seasonal trends that occur at specific times of the year."
            type.name.contains("MA_CROSS") -> "Detects when short-term and long-term moving averages cross, indicating potential trend changes."
            type.name.contains("VOLUME") -> "Correlates price movements with trading volume to identify volume-driven patterns."
            type.name.contains("VOLATILITY") -> "Measures price volatility patterns to predict periods of high or low market movement."
            type.name.contains("SUPPORT") || type.name.contains("RESISTANCE") -> "Identifies price levels where the asset repeatedly bounces or struggles to break through."
            type.name.contains("SEASONAL") -> "Detects patterns that occur during specific seasons or quarters of the year."
            type.name.contains("CONSECUTIVE") -> "Recognizes streaks of consecutive winning or losing days."
            type.name.contains("SPIKE") || type.name.contains("DROP") -> "Identifies significant sudden price movements beyond normal volatility."
            else -> "Uses advanced statistical methods to detect repeatable patterns in price data."
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val KEY_TIMESTAMPS = "timestamps"
    }
}
