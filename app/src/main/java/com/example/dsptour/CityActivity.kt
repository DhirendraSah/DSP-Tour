package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class CityActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter2
    private val cityPlaceList = mutableListOf<Place2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

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

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        placeAdapter = PlaceAdapter2(cityPlaceList) { place, action ->
            handlePlaceAction(place, action)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = placeAdapter

        // Load city places from Firestore
        loadCityPlacesFromFirestore()

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.City -> {
                    Toast.makeText(this, "Already in City", Toast.LENGTH_SHORT).show()
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
    }

    // Function to load city places from Firestore
    private fun loadCityPlacesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("places")
            .whereEqualTo("category", "City") // Fetch only City category
            .get()
            .addOnSuccessListener { result ->
                cityPlaceList.clear()
                for (document in result) {
                    val place = document.toObject<Place2>()
                    cityPlaceList.add(place)
                }
                placeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("CityActivity", "Error loading city places", e)
                Toast.makeText(this, "Failed to load city places", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handlePlaceAction(place: Place2, actionType: PlaceAdapter2.ActionType) {
        when (actionType) {
            PlaceAdapter2.ActionType.LIKE -> {
                // Increment and display like count
                //toggleLike(place)

            }

            PlaceAdapter2.ActionType.COMMENT -> {

                // openCommentDialog(place)

            }

            PlaceAdapter2.ActionType.SHARE -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out ${place.name} at ${place.location}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share ${place.name}"))
            }

            PlaceAdapter2.ActionType.BOOK -> {
                val intent = Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra("PLACE_NAME", place.name)
                    putExtra("PLACE_PRICE", place.price)
                    putExtra("PLACE_IMAGE",place.imageUri)
                }
                startActivity(intent)
            }

            PlaceAdapter2.ActionType.LOCATION -> TODO()
        }
    }
}
