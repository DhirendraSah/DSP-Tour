package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsptour.databinding.ActivityBookingRequestBinding
import com.google.firebase.firestore.FirebaseFirestore

data class BookingRequest(
    val placeName: String = "",
    val date: String = "",
    val price: String = "",
    val meetingPoint: String = "",
    val requestId: String = "",
    var status: String = "" // New field to track booking status
)

class BookingRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingRequestBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private val bookingList = mutableListOf<BookingRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar
        setSupportActionBar(binding.adminToolbar)

        // Configure Drawer Toggle
        drawerToggle = ActionBarDrawerToggle(
            this, binding.adminDrawerLayout, binding.adminToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.adminDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = BookingRequestAdapter(bookingList)
        recyclerView.adapter = adapter

        // Fetch booking requests
        firestore.collection("booking_requests")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val bookingRequest = document.toObject(BookingRequest::class.java).copy(
                        requestId = document.id // Track the Firestore document ID
                    )
                    bookingList.add(bookingRequest)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> { /* Handle Dashboard Navigation */ }
            R.id.nav_logout -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.nav_viewPlace -> {
                startActivity(Intent(this, ViewPlaceActivity::class.java))
                finish()
            }
            R.id.nav_confirm -> {
                startActivity(Intent(this, BookingRequestActivity::class.java))
                finish()
            }
        }
        binding.adminDrawerLayout.closeDrawers()
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    private inner class BookingRequestAdapter(private val bookings: MutableList<BookingRequest>) :
        RecyclerView.Adapter<BookingRequestAdapter.BookingViewHolder>() {

        inner class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val placeName: TextView = view.findViewById(R.id.text_place_name)
            val date: TextView = view.findViewById(R.id.text_date)
            val price: TextView = view.findViewById(R.id.text_price)
            val meetingPoint: TextView = view.findViewById(R.id.text_meeting_point)
            val statusText: TextView = view.findViewById(R.id.text_status) // New TextView to show status
            val okIcon: ImageView = view.findViewById(R.id.icon_ok)
            val cancelIcon: ImageView = view.findViewById(R.id.icon_cancel)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.booking_request_item, parent, false)
            return BookingViewHolder(view)
        }

        override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
            val booking = bookings[position]
            holder.placeName.text = booking.placeName
            holder.date.text = booking.date
            holder.price.text = booking.price
            holder.meetingPoint.text = booking.meetingPoint

            // Show status if available
            holder.statusText.text = if (booking.status.isNotEmpty()) {
                "Status: ${booking.status.capitalize()}"
            } else {
                "Status: Pending"
            }

            // Hide icons if already approved
            if (booking.status == "approved") {
                holder.okIcon.visibility = View.GONE
                holder.cancelIcon.visibility = View.GONE
            } else {
                holder.okIcon.visibility = View.VISIBLE
                holder.cancelIcon.visibility = View.VISIBLE
            }

            holder.okIcon.setOnClickListener {
                // Handle OK click - Update Firestore status and remove item
                updateBookingStatus(booking.requestId, "approved") {
                    booking.status = "approved"
                    notifyItemChanged(position)
                }
            }

            holder.cancelIcon.setOnClickListener {
                // Handle Cancel click - Remove from Firestore
                deleteBookingRequest(booking.requestId) {
                    // Remove item from the list and update the RecyclerView
                    bookings.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, bookings.size)
                }
            }
        }

        override fun getItemCount(): Int = bookings.size

        private fun updateBookingStatus(requestId: String, status: String, onSuccess: () -> Unit) {
            firestore.collection("booking_requests").document(requestId)
                .update("status", status)
                .addOnSuccessListener {
                    Toast.makeText(this@BookingRequestActivity, "Booking $status", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(this@BookingRequestActivity, "Failed to update booking", Toast.LENGTH_SHORT).show()
                }
        }

        private fun deleteBookingRequest(requestId: String, onSuccess: () -> Unit) {
            firestore.collection("booking_requests").document(requestId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this@BookingRequestActivity, "Booking denied", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(this@BookingRequestActivity, "Failed to delete booking", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
