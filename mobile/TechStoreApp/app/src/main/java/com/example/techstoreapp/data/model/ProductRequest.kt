package com.example.techstoreapp.data.model

data class ProductRequest(
    val categoryId: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val imageUrl: String?
)