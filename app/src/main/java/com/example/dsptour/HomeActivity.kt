package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.activity.OnBackPressedCallback
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

        // "Book Now" button clicks
        findViewById<Button>(R.id.book_city).setOnClickListener {
            navigateToConfirmation("Kathmandu", "100")
        }

        findViewById<Button>(R.id.book_temple).setOnClickListener {
            navigateToConfirmation("Pashupatinath Temple", "50")
        }

        findViewById<Button>(R.id.book_park).setOnClickListener {
            navigateToConfirmation("Chitwan National Park", "75")
        }

        findViewById<Button>(R.id.book_pokhara).setOnClickListener {
            navigateToConfirmation("Pokhara", "120")
        }

        findViewById<Button>(R.id.book_historical).setOnClickListener {
            navigateToConfirmation("Bhaktapur Durbar Square", "90")
        }

        findViewById<Button>(R.id.book_national_park).setOnClickListener {
            navigateToConfirmation("Sagarmatha National Park", "130")
        }

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
    }

    private fun navigateToConfirmation(destination: String, price: String) {
        val intent = Intent(this, ConfirmationActivity::class.java)
        intent.putExtra("destination", destination)
        intent.putExtra("price", price)
        startActivity(intent)
    }
}
