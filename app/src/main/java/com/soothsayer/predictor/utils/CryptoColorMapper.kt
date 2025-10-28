package com.soothsayer.predictor.utils

import android.graphics.Color

/**
 * Color utilities for crypto assets
 * Each cryptocurrency gets a unique, visually distinct color
 */
object CryptoColorMapper {
    
    private val colorMap = mapOf(
        "BTCUSDT" to "#F7931A",  // Bitcoin Orange
        "ETHUSDT" to "#627EEA",  // Ethereum Blue
        "BNBUSDT" to "#F3BA2F",  // Binance Yellow
        "ADAUSDT" to "#0033AD",  // Cardano Blue
        "DOGEUSDT" to "#C2A633", // Dogecoin Gold
        "XRPUSDT" to "#23292F",  // Ripple Dark Gray (changed to lighter for visibility)
        "SOLUSDT" to "#14F195",  // Solana Green
        "DOTUSDT" to "#E6007A",  // Polkadot Pink
        "MATICUSDT" to "#8247E5", // Polygon Purple
        "LTCUSDT" to "#345D9D"   // Litecoin Blue
    )
    
    private val fallbackColors = listOf(
        "#BB86FC", // Purple
        "#03DAC5", // Teal
        "#CF6679", // Pink
        "#FF6F00", // Orange
        "#00BFA5", // Cyan
        "#6200EE", // Deep Purple
        "#018786", // Dark Teal
        "#B00020", // Red
        "#FFAB00", // Amber
        "#00C853"  // Green
    )
    
    /**
     * Get color for a specific crypto symbol
     * @param symbol The crypto pair symbol (e.g., "BTCUSDT")
     * @return Color integer
     */
    fun getColorForSymbol(symbol: String): Int {
        val colorHex = colorMap[symbol] ?: fallbackColors[Math.abs(symbol.hashCode()) % fallbackColors.size]
        return Color.parseColor(colorHex)
    }
    
    /**
     * Get color hex string for a specific crypto symbol
     * @param symbol The crypto pair symbol (e.g., "BTCUSDT")
     * @return Hex color string (e.g., "#F7931A")
     */
    fun getColorHexForSymbol(symbol: String): String {
        return colorMap[symbol] ?: fallbackColors[Math.abs(symbol.hashCode()) % fallbackColors.size]
    }
    
    /**
     * Get a lighter version of the crypto color for fills/backgrounds
     * @param symbol The crypto pair symbol
     * @return Color integer with reduced alpha
     */
    fun getLightColorForSymbol(symbol: String): Int {
        val baseColor = getColorForSymbol(symbol)
        val alpha = 50 // ~20% opacity
        return Color.argb(
            alpha,
            Color.red(baseColor),
            Color.green(baseColor),
            Color.blue(baseColor)
        )
    }
}
