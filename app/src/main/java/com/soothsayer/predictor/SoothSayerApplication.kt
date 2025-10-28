package com.soothsayer.predictor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for SoothSayer
 * Entry point for Hilt dependency injection
 */
@HiltAndroidApp
class SoothSayerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any global configurations here
    }
}
