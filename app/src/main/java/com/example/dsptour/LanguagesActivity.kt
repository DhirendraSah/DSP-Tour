package com.example.dsptour

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LanguagesActivity : AppCompatActivity() {

    private lateinit var radioEnglish: RadioButton
    private lateinit var radioNepali: RadioButton
    private lateinit var radioHindi: RadioButton
    private lateinit var btnSaveLanguage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)

        // Initialize UI elements
        radioEnglish = findViewById(R.id.radio_english)
        radioNepali = findViewById(R.id.radio_nepali)
        radioHindi = findViewById(R.id.radio_hindi)
        btnSaveLanguage = findViewById(R.id.btn_save_language)

        // Load current language preference
        loadLanguagePreference()

        // Set click listener for the save button
        btnSaveLanguage.setOnClickListener {
            saveLanguagePreference()
        }
    }

    private fun loadLanguagePreference() {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en") // Default to English

        when (language) {
            "en" -> radioEnglish.isChecked = true
            "ne" -> radioNepali.isChecked = true
            "hi" -> radioHindi.isChecked = true
        }
    }

    private fun saveLanguagePreference() {
        val selectedLanguage: String = when {
            radioEnglish.isChecked -> "en"
            radioNepali.isChecked -> "ne"
            radioHindi.isChecked -> "hi"
            else -> "en" // Default
        }

        // Save language preference
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", selectedLanguage)
        editor.apply()

        // Change app language
        setAppLocale(selectedLanguage)

        // Restart the activity to apply changes
        val intent = Intent(this, MainActivity::class.java) // Redirect to your main activity or the desired one
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish() // Finish this activity
    }

    private fun setAppLocale(localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale) // API 24
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
