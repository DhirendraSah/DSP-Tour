package com.example.dsptour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDestination: TextView = view.findViewById(R.id.text_destination)
        val textDate: TextView = view.findViewById(R.id.text_date)
        val textMeetingPoint: TextView = view.findViewById(R.id.text_meeting_point)
        val textPrice: TextView = view.findViewById(R.id.text_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.textDestination.text = "Destination: ${order.destination}"
        holder.textDate.text = "Date: ${order.date}"
        holder.textMeetingPoint.text = "Meeting Point: ${order.meetingPoint}"
        holder.textPrice.text = "Price: ${order.price}"
    }

    override fun getItemCount() = orders.size
}
