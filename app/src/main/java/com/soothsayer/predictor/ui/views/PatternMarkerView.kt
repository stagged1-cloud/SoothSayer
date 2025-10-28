package com.soothsayer.predictor.ui.views

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.soothsayer.predictor.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PatternMarkerView(
    context: Context,
    private val timestamps: List<Long>
) : MarkerView(context, R.layout.marker_view_pattern) {

    private val tvOccurrence: TextView = findViewById(R.id.tv_occurrence)
    private val tvDate: TextView = findViewById(R.id.tv_date)
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = it.x.toInt()
            if (index >= 0 && index < timestamps.size) {
                val occurrenceNumber = index + 1 // Occurrence number is 1-based
                tvOccurrence.text = "Occurrence #$occurrenceNumber"
                tvDate.text = dateFormat.format(Date(timestamps[index]))
            } else {
                // Fallback if timestamps not yet populated
                tvOccurrence.text = "Occurrence"
                tvDate.text = ""
            }
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
