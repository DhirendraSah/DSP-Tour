package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmation)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Set up Drawer toggle (hamburger icon)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    Toast.makeText(this, "Home is clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.City -> {
                    startActivity(Intent(this, CityActivity::class.java))
                }
                R.id.Temple -> {
                    startActivity(Intent(this, TempleActivity::class.java))
                }
                R.id.Park -> {
                    startActivity(Intent(this, ParkActivity::class.java))
                }
                R.id.setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
                R.id.Share -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share this app with:"))
                }
                R.id.logout -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Handle back button to close the navigation drawer if open
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })

        // Get data from intent
        val destination = intent.getStringExtra("destination")
        val price = intent.getStringExtra("price")

        // Set destination and price in the UI
        findViewById<TextView>(R.id.text_destination).text = destination
        findViewById<TextView>(R.id.text_price).text = "Price: $$price"

        // Handle Confirm button click
        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val selectedDate = findViewById<EditText>(R.id.date_input).text.toString()
            val meetingPointGroup = findViewById<RadioGroup>(R.id.meeting_point_group)
            val selectedMeetingPointId = meetingPointGroup.checkedRadioButtonId
            val meetingPoint = findViewById<RadioButton>(selectedMeetingPointId)?.text.toString()

            // Ensure date and meeting point are selected
            if (selectedDate.isNotEmpty() && meetingPoint.isNotEmpty()) {
                // Save the data to Firebase Realtime Database
                saveBookingToFirebase(destination, price, selectedDate, meetingPoint)

                // Proceed to PaymentActivity
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("destination", destination)
                intent.putExtra("price", price)
                intent.putExtra("date", selectedDate)
                intent.putExtra("meeting_point", meetingPoint)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save booking details in Firebase Realtime Database
    private fun saveBookingToFirebase(destination: String?, price: String?, date: String, meetingPoint: String) {
        val userId = auth.currentUser?.uid ?: return

        // Create a booking map with the data
        val bookingData = hashMapOf(
            "userId" to userId,
            "destination" to destination,
            "price" to price,
            "date" to date,
            "meetingPoint" to meetingPoint
        )

        // Get a reference to the "bookings" node in Firebase Realtime Database
        val bookingsRef = database.getReference("bookings").child(userId).push()

        // Save the booking data under the userId node
        bookingsRef.setValue(bookingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
