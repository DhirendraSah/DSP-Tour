package com.example.dsptour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orderList: List<OrderItem>,
    private val onCancelClick: (String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDestination: TextView = itemView.findViewById(R.id.textViewDestination)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewMeetingPoint: TextView = itemView.findViewById(R.id.textViewMeetingPoint)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val textViewPaymentMode: TextView = itemView.findViewById(R.id.textViewPaymentMode)
        private val buttonCancel: Button = itemView.findViewById(R.id.buttonCancel)

        fun bind(order: OrderItem) {
            textViewDestination.text = order.destination
            textViewDate.text = order.date
            textViewMeetingPoint.text = order.meetingPoint
            textViewPrice.text = order.price
            textViewPaymentMode.text = order.paymentMode

            buttonCancel.setOnClickListener {
                onCancelClick(order.id)
            }
        }
    }
}
