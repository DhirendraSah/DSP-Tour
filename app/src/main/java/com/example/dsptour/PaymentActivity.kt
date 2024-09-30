package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge layout
        setContentView(R.layout.activity_payment)

        // Initialize UI elements
        val payButton: Button = findViewById(R.id.btn_pay)
        val paytmCheckBox: CheckBox = findViewById(R.id.checkbox_1)
        val phonePayCheckBox: CheckBox = findViewById(R.id.checkbox_2)
        val googlePayCheckBox: CheckBox = findViewById(R.id.checkbox_3)
        val cashOnMeetCheckBox: CheckBox = findViewById(R.id.checkbox_4)

        // Set up click listener for the Pay button
        payButton.setOnClickListener {
            when {
                paytmCheckBox.isChecked -> processPaytmPayment()
                phonePayCheckBox.isChecked -> processPhonePayPayment()
                googlePayCheckBox.isChecked -> processGooglePayPayment()
                cashOnMeetCheckBox.isChecked -> processCashOnMeetPayment()
                else -> {
                    // Show a message if no payment method is selected
                    Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processPaytmPayment() {
        // Logic for Paytm payment
        Toast.makeText(this, "Proceeding with Paytm payment", Toast.LENGTH_SHORT).show()
        // Simulate payment success (replace with actual SDK integration)
        redirectToHome()
    }

    private fun processPhonePayPayment() {
        // Logic for Phone Pay payment
        Toast.makeText(this, "Proceeding with Phone Pay payment", Toast.LENGTH_SHORT).show()
        // Simulate payment success (replace with actual SDK integration)
        redirectToHome()
    }

    private fun processGooglePayPayment() {
        // Logic for Google Pay payment
        Toast.makeText(this, "Proceeding with Google Pay payment", Toast.LENGTH_SHORT).show()
        // Simulate payment success (replace with actual SDK integration)
        redirectToHome()
    }

    private fun processCashOnMeetPayment() {
        // Logic for Cash on Meet payment
        Toast.makeText(this, "Proceeding with Cash on Meet", Toast.LENGTH_SHORT).show()
        // Simulate payment success (replace with actual confirmation)
        redirectToHome()
    }

    private fun redirectToHome() {
        // Start HomeActivity after successful payment
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
        startActivity(intent)
        finish() // Close the PaymentActivity if needed
    }
}
