package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Retrieve data from intent
        val placeName = intent.getStringExtra("PLACE_NAME")
        val placePrice = intent.getStringExtra("PLACE_PRICE")

        // Set text views with received data
        findViewById<TextView>(R.id.text_destination).text = placeName
        findViewById<TextView>(R.id.text_price).text = placePrice

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Confirm button listener
        findViewById<Button>(R.id.confirm_button).setOnClickListener {
            val selectedDate = findViewById<EditText>(R.id.date_input).text.toString()
            val meetingPointGroup = findViewById<RadioGroup>(R.id.meeting_point_group)
            val selectedMeetingPointId = meetingPointGroup.checkedRadioButtonId
            val meetingPoint = findViewById<RadioButton>(selectedMeetingPointId)?.text.toString()

            // Ensure all fields are filled
            if (selectedDate.isNotEmpty() && meetingPoint.isNotEmpty()) {
                if (isValidDate(selectedDate)) {
                    // Check if booking with same place and date already exists
                    firestore.collection("booking_requests")
                        .whereEqualTo("placeName", placeName)
                        .whereEqualTo("date", selectedDate)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                // No duplicate booking, proceed to store booking request
                                createBookingRequest(placeName, placePrice, selectedDate, meetingPoint)
                            } else {
                                // Booking already exists for the same date
                                Toast.makeText(this, "This date is already taken for the selected place. Please choose another date.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error checking for existing bookings.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Please select a valid date (today or later).", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to validate that the date is today or a future date
    private fun isValidDate(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            val selectedDate = dateFormat.parse(dateString)
            val currentDate = Calendar.getInstance().time
            selectedDate != null && !selectedDate.before(currentDate)
        } catch (e: Exception) {
            false
        }
    }

    // Function to create a booking request in Firestore
    private fun createBookingRequest(placeName: String?, placePrice: String?, date: String, meetingPoint: String) {
        val bookingRequest = hashMapOf(
            "placeName" to placeName,
            "date" to date,
            "meetingPoint" to meetingPoint,
            "status" to "pending" // Initial status set to pending
        )

        // Store booking request in Firestore
        firestore.collection("booking_requests").add(bookingRequest)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Booking request sent. Awaiting admin approval.", Toast.LENGTH_SHORT).show()
                // Listen for admin approval or denial
                listenForAdminApproval(documentReference.id, placeName, placePrice, date, meetingPoint)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send booking request.", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to listen for status changes from admin
    private fun listenForAdminApproval(requestId: String, placeName: String?, placePrice: String?, date: String, meetingPoint: String) {
        listenerRegistration = firestore.collection("booking_requests").document(requestId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("status")
                    when (status) {
                        "approved" -> {
                            // Navigate to PaymentActivity if approved
                            val intent = Intent(this@ConfirmationActivity, PaymentActivity::class.java).apply {
                                putExtra("destination", placeName)
                                putExtra("date", date)
                                putExtra("meeting_point", meetingPoint)
                                putExtra("price", placePrice)  // Pass the price to PaymentActivity
                            }
                            startActivity(intent)
                            finish() // Close current activity
                        }
                        "denied" -> {
                            // Notify user if booking is denied
                            Toast.makeText(this@ConfirmationActivity, "Booking denied. Please try another date.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the listener when activity is destroyed
        listenerRegistration?.remove()
    }
}
