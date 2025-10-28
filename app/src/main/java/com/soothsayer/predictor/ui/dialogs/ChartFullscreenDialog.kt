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
import com.soothsayer.predictor.R
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
    private var symbol: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        
        // Restore state if available
        arguments?.let {
            priceData = it.getParcelableArrayList(ARG_PRICE_DATA) ?: ArrayList()
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
            marker = PriceMarkerView(requireContext(), priceData)
            
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
        
        binding.fullscreenChart.data = LineData(dataSet)
        
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
        private const val ARG_SYMBOL = "symbol"
        
        fun newInstance(priceData: List<PriceData>, symbol: String): ChartFullscreenDialog {
            return ChartFullscreenDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PRICE_DATA, ArrayList(priceData))
                    putString(ARG_SYMBOL, symbol)
                }
            }
        }
    }
}
