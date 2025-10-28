package com.soothsayer.predictor.data.local

import androidx.room.TypeConverter
import com.soothsayer.predictor.data.models.PatternType

/**
 * Type converters for Room database
 */
class Converters {
    
    @TypeConverter
    fun fromPatternType(value: PatternType): String {
        return value.name
    }
    
    @TypeConverter
    fun toPatternType(value: String): PatternType {
        return try {
            PatternType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            PatternType.DAILY_PATTERN // Default fallback
        }
    }
}
