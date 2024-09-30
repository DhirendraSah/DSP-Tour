package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class SettingActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

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
                    startActivity(Intent(this, HomeActivity::class.java))
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
                    Toast.makeText(this, "Setting is clicked", Toast.LENGTH_SHORT).show()
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
                    finish()  // Finish the activity to remove it from the back stack
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Handle back button to close the navigation drawer if open
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    finish()  // or use `super.onBackPressed()` if you want the default behavior
                }
            }
        })
    }

    // Function to open ProfileActivity
    fun openProfile(view: android.view.View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    // Function to open OrdersActivity
    fun openOrders(view: android.view.View) {
        val intent = Intent(this, OrderActivity::class.java)
        startActivity(intent)
    }

    // Function to open PaymentActivity
    fun openPayment(view: android.view.View) {
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
    }

    // Function to open LanguagesActivity
    fun openLanguages(view: android.view.View) {
        val intent = Intent(this, LanguagesActivity::class.java)
        startActivity(intent)
    }
}
