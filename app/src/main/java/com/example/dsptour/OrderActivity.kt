package com.example.dsptour

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsptour.databinding.ActivityOrderBinding // Import the generated binding class

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding // Declare the binding variable
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orders: List<Order> // List to hold your orders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater) // Inflate the binding
        setContentView(binding.root) // Set the content view to the binding root

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sample order data (replace this with your actual data)
        orders = listOf(
            Order("City Tour", "2024-09-30", "Indra Chowck", "$100"),
            Order("Temple Tour", "2024-10-01", "Thamel", "$150"),
            // Add more orders as needed
        )

        // Set up RecyclerView
        orderAdapter = OrderAdapter(orders)
        binding.recyclerViewOrders.adapter = orderAdapter // Use the binding to access RecyclerView
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)

        // Set back button click listener
        binding.btnBack.setOnClickListener {
            onBackPressed() // Navigate back when button is pressed
        }
    }
}
