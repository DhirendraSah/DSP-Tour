package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge layout
        setContentView(R.layout.activity_payment)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data passed from the previous activity (ConfirmationActivity)
        val destination = intent.getStringExtra("destination")
        val date = intent.getStringExtra("date")
        val meetingPoint = intent.getStringExtra("meeting_point")
        val price = intent.getStringExtra("price")

        // Initialize UI elements
        val payButton: Button = findViewById(R.id.btn_pay)
        val paytmCheckBox: CheckBox = findViewById(R.id.checkbox_1)
        val phonePayCheckBox: CheckBox = findViewById(R.id.checkbox_2)
        val googlePayCheckBox: CheckBox = findViewById(R.id.checkbox_3)
        val cashOnMeetCheckBox: CheckBox = findViewById(R.id.checkbox_4)

        // Set up click listener for the Pay button
        payButton.setOnClickListener {
            when {
                cashOnMeetCheckBox.isChecked -> processCashOnMeetPayment(destination, date, meetingPoint, price)
                paytmCheckBox.isChecked -> processPaytmPayment(destination, date, meetingPoint, price)
                phonePayCheckBox.isChecked -> processPhonePayPayment(destination, date, meetingPoint, price)
                googlePayCheckBox.isChecked -> processGooglePayPayment(destination, date, meetingPoint, price)
                else -> {
                    // Show a message if no payment method is selected
                    Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processPaytmPayment(destination: String?, date: String?, meetingPoint: String?, price: String?) {
        // Logic for Paytm payment (you can integrate Paytm SDK here)
        Toast.makeText(this, "Proceeding with Paytm payment", Toast.LENGTH_SHORT).show()
        // For demo, simulate a successful payment and store the data in Firestore
        storePaymentDetails(destination, date, meetingPoint, price, "Paytm")
    }

    private fun processPhonePayPayment(destination: String?, date: String?, meetingPoint: String?, price: String?) {
        // Logic for Phone Pay payment (you can integrate PhonePe SDK here)
        Toast.makeText(this, "Proceeding with Phone Pay payment", Toast.LENGTH_SHORT).show()
        // For demo, simulate a successful payment and store the data in Firestore
        storePaymentDetails(destination, date, meetingPoint, price, "PhonePe")
    }

    private fun processGooglePayPayment(destination: String?, date: String?, meetingPoint: String?, price: String?) {
        // Logic for Google Pay payment (you can integrate Google Pay SDK here)
        Toast.makeText(this, "Proceeding with Google Pay payment", Toast.LENGTH_SHORT).show()
        // For demo, simulate a successful payment and store the data in Firestore
        storePaymentDetails(destination, date, meetingPoint, price, "Google Pay")
    }

    private fun processCashOnMeetPayment(destination: String?, date: String?, meetingPoint: String?, price: String?) {
        // Logic for Cash on Meet payment
        Toast.makeText(this, "Proceeding with Cash on Meet", Toast.LENGTH_SHORT).show()
        // For Cash on Meet, directly store the data in Firestore
        storePaymentDetails(destination, date, meetingPoint, price, "Cash on Meet")
    }

    private fun storePaymentDetails(destination: String?, date: String?, meetingPoint: String?, price: String?, paymentMode: String) {
        // Store payment details in Firestore
        val paymentDetails = hashMapOf(
            "destination" to destination,
            "date" to date,
            "meetingPoint" to meetingPoint,
            "price" to price,
            "paymentMode" to paymentMode,
            "status" to "Paid" // Update status to "Paid"
        )

        firestore.collection("booking_requests")
            .add(paymentDetails)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking confirmed and payment recorded.", Toast.LENGTH_SHORT).show()
                // Optionally navigate to another activity (e.g., Thank You page)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to process payment. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
