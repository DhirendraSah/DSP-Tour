package com.example.dsptour

data class OrderItem(
    val destination: String = "",
    val date: String = "",
    val meetingPoint: String = "",
    val price: String = "",
    val paymentMode: String = "",
    var id: String = "" // Use Firestore document ID if needed
)

