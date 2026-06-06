package com.example.techstoreapp.data.model

data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val active: Boolean
)