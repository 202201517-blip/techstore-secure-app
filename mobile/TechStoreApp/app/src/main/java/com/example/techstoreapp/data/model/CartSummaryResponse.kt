package com.example.techstoreapp.data.model

data class CartSummaryResponse(
    val items: List<CartItemResponse>,
    val totalItems: Int,
    val totalAmount: Double
)
