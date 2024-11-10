package com.example.dsptour
data class Place2(
    val name: String = "",
    val price: String = "",
    val imageUri: String = "",
    val location: String = "",
    var likeCount: Int = 0,
    var isLiked: Boolean = false
)