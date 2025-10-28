package com.soothsayer.predictor.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.soothsayer.predictor.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Splash Screen Activity
 * Displays the SoothSayer branding before loading the main app
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    
    private val splashTimeOut: Long = 2500 // 2.5 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Navigate to MainActivity after splash timeout
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTimeOut)
    }
}
