package com.example.techstoreapp.data.model

data class ProductResponse(
    val id: Long,
    val categoryId: Long?,
    val categoryName: String?,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val imageUrl: String?,
    val active: Boolean
)