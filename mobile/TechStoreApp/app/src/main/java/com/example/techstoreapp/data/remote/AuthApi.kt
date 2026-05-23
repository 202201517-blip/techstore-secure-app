package com.example.techstoreapp.data.remote

import com.example.techstoreapp.data.model.AuthResponse
import com.example.techstoreapp.data.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}