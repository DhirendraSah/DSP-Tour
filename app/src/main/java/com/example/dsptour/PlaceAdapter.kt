package com.example.dsptour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PlaceAdapter(
    private val placesList: MutableList<Place>,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeImage: ImageView = itemView.findViewById(R.id.place_image)
        val placeName: TextView = itemView.findViewById(R.id.place_name)
        val placePrice: TextView = itemView.findViewById(R.id.place_price)
        val placeCategory: TextView = itemView.findViewById(R.id.place_category)
        val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(place.imageUri)
            .into(holder.placeImage)

        // Set text fields
        holder.placeName.text = place.name
        holder.placePrice.text = "Price: ${place.price}"
        holder.placeCategory.text = "Category: ${place.category}"

        // Set remove button action
        holder.btnRemove.setOnClickListener {
            firestore.collection("places").document(place.id)
                .delete()
                .addOnSuccessListener {
                    placesList.removeAt(holder.bindingAdapterPosition)
                    notifyItemRemoved(holder.bindingAdapterPosition)
                    Toast.makeText(holder.itemView.context, "Place removed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Failed to remove place", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int {
        return placesList.size
    }
}
