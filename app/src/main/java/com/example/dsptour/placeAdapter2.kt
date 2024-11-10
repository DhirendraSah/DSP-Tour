package com.example.dsptour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceAdapter2(
    private val places: List<Place2>,
    private val clickListener: (Place2, ActionType) -> Unit
) : RecyclerView.Adapter<PlaceAdapter2.PlaceViewHolder>() {

    enum class ActionType {
        LIKE, COMMENT, SHARE, LOCATION, BOOK
    }

    inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.text_name)
        val priceTextView: TextView = view.findViewById(R.id.text_price)
        val imageView: ImageView = view.findViewById(R.id.image_place)
        val likeButton: ImageButton = view.findViewById(R.id.button_like)
        val commentButton: ImageButton = view.findViewById(R.id.button_comment)
        val shareButton: ImageButton = view.findViewById(R.id.button_share)
        val bookButton: Button = view.findViewById(R.id.button_book_place)

        fun bind(place: Place2) {
            nameTextView.text = place.name
            priceTextView.text = place.price
            // Load image using Glide or Picasso
            Glide.with(imageView.context).load(place.imageUri).into(imageView)

            // Set click listeners

            likeButton.setOnClickListener { clickListener(place, ActionType.LIKE) }
            commentButton.setOnClickListener { clickListener(place, ActionType.COMMENT) }
            shareButton.setOnClickListener { clickListener(place, ActionType.SHARE) }
            bookButton.setOnClickListener { clickListener(place, ActionType.BOOK) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item2, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount() = places.size
}

