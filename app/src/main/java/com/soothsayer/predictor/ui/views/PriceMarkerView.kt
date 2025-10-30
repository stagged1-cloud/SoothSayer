package com.soothsayer.predictor.ui.views

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.data.models.PriceData
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PriceMarkerView(
    context: Context,
    private var priceData: List<PriceData>,
    private var patterns: List<Pattern> = emptyList()
) : MarkerView(context, R.layout.marker_view_price) {

    private val tvPrice: TextView = findViewById(R.id.tv_price)
    private val tvDate: TextView = findViewById(R.id.tv_date)
    private val tvPattern: TextView? = try { findViewById(R.id.tv_pattern) } catch (e: Exception) { null }
    private val priceFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    fun updateData(newData: List<PriceData>, newPatterns: List<Pattern> = emptyList()) {
        priceData = newData
        patterns = newPatterns
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = it.x.toInt()
            if (index >= 0 && index < priceData.size) {
                val data = priceData[index]
                tvPrice.text = priceFormat.format(data.close)
                tvDate.text = dateFormat.format(Date(data.timestamp))
                
                // Check if this point has a pattern marker
                val timestamp = data.timestamp
                val matchingPattern = patterns.firstOrNull { pattern ->
                    Math.abs(pattern.lastOccurrence - timestamp) < 86400000 // Within 1 day
                }
                
                tvPattern?.text = matchingPattern?.let { pattern ->
                    "${formatPatternType(pattern.patternType)} (${(pattern.confidence * 100).toInt()}%)"
                } ?: ""
            }
        }
        super.refreshContent(e, highlight)
    }

    private fun formatPatternType(type: com.soothsayer.predictor.data.models.PatternType): String {
        return type.name.replace("_", " ").lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    override fun getOffset(): MPPointF {
        // Adjust tooltip position to prevent right-side cutoff
        return MPPointF(-(width.toFloat()), -height.toFloat())
    }
}
