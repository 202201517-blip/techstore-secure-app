package com.example.techstoreapp.data.model

data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productDescription: String?,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double,
    val imageUrl: String?
)
