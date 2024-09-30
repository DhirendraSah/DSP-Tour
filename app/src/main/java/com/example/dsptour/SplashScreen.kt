package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    // Duration for which the splash screen will be visible (3000ms = 3 seconds)
    private val SPLASH_DISPLAY_LENGTH: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashcsreen) // Replace with your splash screen layout

        // Use a Handler to run the splash screen for the specified duration
        Handler(Looper.getMainLooper()).postDelayed({
            // After the delay, start MainActivity
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            // Close the SplashScreen so the user can't go back to it
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}
