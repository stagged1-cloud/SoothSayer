package com.soothsayer.predictor.ui.views

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.PriceData
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PriceMarkerView(
    context: Context,
    private val priceData: List<PriceData>
) : MarkerView(context, R.layout.marker_view_price) {

    private val tvPrice: TextView = findViewById(R.id.tv_price)
    private val tvDate: TextView = findViewById(R.id.tv_date)
    private val priceFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = it.x.toInt()
            if (index >= 0 && index < priceData.size) {
                val data = priceData[index]
                tvPrice.text = priceFormat.format(data.close)
                tvDate.text = dateFormat.format(Date(data.timestamp))
            }
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
