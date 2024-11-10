package com.example.dsptour

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dsptour.databinding.ActivityViewPlaceBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPlaceBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeAdapter: PlaceAdapter
    private val placesList = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        placeAdapter = PlaceAdapter(placesList, firestore)
        binding.recyclerViewPlaces.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPlaces.adapter = placeAdapter

        // Load places data
        loadPlacesFromFirestore()
    }

    private fun loadPlacesFromFirestore() {
        firestore.collection("places")
            .get()
            .addOnSuccessListener { result ->
                placesList.clear() // Clear existing list if any
                for (document in result) {
                    val place = document.toObject(Place::class.java)
                    place.id = document.id
                    placesList.add(place)
                }
                placeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load places", Toast.LENGTH_SHORT).show()
            }
    }
}
