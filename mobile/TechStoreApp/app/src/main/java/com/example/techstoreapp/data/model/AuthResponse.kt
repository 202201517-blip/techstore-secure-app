package com.example.techstoreapp.data.model

data class AuthResponse(
    val message: String,
    val email: String?,
    val role: String?,
    val token: String?
)
