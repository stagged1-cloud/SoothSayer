package com.soothsayer.predictor.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soothsayer.predictor.R
import com.soothsayer.predictor.data.models.Pattern
import com.soothsayer.predictor.databinding.ItemPatternBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Adapter for displaying detected patterns in a RecyclerView
 */
class PatternAdapter(
    private val onPatternClick: (Pattern) -> Unit
) : ListAdapter<Pattern, PatternAdapter.PatternViewHolder>(PatternDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder {
        val binding = ItemPatternBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PatternViewHolder(binding, onPatternClick)
    }
    
    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PatternViewHolder(
        private val binding: ItemPatternBinding,
        private val onPatternClick: (Pattern) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(pattern: Pattern) {
            binding.apply {
                root.setOnClickListener { onPatternClick(pattern) }
                
                patternType.text = formatPatternType(pattern.patternType)
                patternDescription.text = pattern.description
                confidenceBadge.text = "${(pattern.confidence * 100).toInt()}%"
                frequencyValue.text = "${pattern.frequency} times"
                lastOccurrenceValue.text = formatTimeAgo(pattern.lastOccurrence)
                nextOccurrenceValue.text = pattern.predictedNextOccurrence?.let {
                    formatTimeUntil(it)
                } ?: "N/A"
                
                // Set confidence badge color
                val confidenceColor = when {
                    pattern.confidence >= 0.8 -> R.color.confidence_high
                    pattern.confidence >= 0.6 -> R.color.confidence_medium
                    else -> R.color.confidence_low
                }
                confidenceBadge.setBackgroundColor(
                    root.context.getColor(confidenceColor)
                )
            }
        }
        
        private fun formatPatternType(type: com.soothsayer.predictor.data.models.PatternType): String {
            return type.name
                .split("_")
                .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
        }
        
        private fun formatTimeAgo(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            
            return when {
                days == 0L -> "Today"
                days == 1L -> "Yesterday"
                days < 7 -> "$days days ago"
                days < 30 -> "${days / 7} weeks ago"
                days < 365 -> "${days / 30} months ago"
                else -> "${days / 365} years ago"
            }
        }
        
        private fun formatTimeUntil(timestamp: Long): String {
            val diff = timestamp - System.currentTimeMillis()
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            
            return when {
                days < 0 -> "Overdue"
                days == 0L -> "Today"
                days == 1L -> "Tomorrow"
                days < 7 -> "In $days days"
                days < 30 -> "In ${days / 7} weeks"
                days < 365 -> "In ${days / 30} months"
                else -> "In ${days / 365} years"
            }
        }
    }
    
    private class PatternDiffCallback : DiffUtil.ItemCallback<Pattern>() {
        override fun areItemsTheSame(oldItem: Pattern, newItem: Pattern): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Pattern, newItem: Pattern): Boolean {
            return oldItem == newItem
        }
    }
}
