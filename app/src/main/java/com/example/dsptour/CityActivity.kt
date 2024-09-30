package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

class CityActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enable edge-to-edge layout
        setContentView(R.layout.activity_city)

        // Set up the Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up edge-to-edge insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up the NavigationView
        navigationView = findViewById(R.id.nav_view)

        // Set up the Drawer toggle (hamburger icon) to open/close the drawer
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
        findViewById<Button>(R.id.book_city2).setOnClickListener {
            navigateToConfirmation("Pokhara", "100")
        }
        findViewById<Button>(R.id.book_city3).setOnClickListener {
            navigateToConfirmation("Mustang", "120")
        }
        findViewById<Button>(R.id.book_city4).setOnClickListener {
            navigateToConfirmation("Lalitpur", "100")
        }

        // Handle menu item clicks in NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.City -> {
                    // Stay on CityActivity
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
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share this app with:"))
                }
                R.id.logout -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()  // Optional: finish CityActivity so it's removed from the back stack
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
                    finish()  // or use `super.onBackPressed()` if you want the default behavior
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
