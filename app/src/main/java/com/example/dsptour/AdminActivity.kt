package com.example.dsptour

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.dsptour.databinding.ActivityAdminBinding
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Use View Binding
        binding = ActivityAdminBinding.inflate(layoutInflater)
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

        // Set Navigation Item Listener
        binding.adminNavView.setNavigationItemSelectedListener(this)

        // Set click listeners for CardViews
        binding.cardAddCity.setOnClickListener {
           startActivity(Intent(this, AddPlaceActivity::class.java))
        }

        binding.cardAddTemple.setOnClickListener {
            startActivity(Intent(this, AddPlaceActivity::class.java))
        }
        binding.cardAddPark.setOnClickListener{
            startActivity(Intent(this, AddPlaceActivity::class.java))
        }

        // Add similar listeners for Parks, Flights, and Accommodation
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Handle Dashboard Navigation
            }
            R.id.nav_logout -> {
                // Handle Logout
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.nav_viewPlace -> {
                startActivity(Intent(this,ViewPlaceActivity::class.java))
                finish()
            }
            R.id.nav_confirm -> {
                startActivity(Intent(this,BookingRequestActivity::class.java))
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
}
