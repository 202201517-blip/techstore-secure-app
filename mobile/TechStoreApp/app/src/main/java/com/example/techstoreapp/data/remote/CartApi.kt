package com.example.techstoreapp.data.remote

import com.example.techstoreapp.data.model.CartItemRequest
import com.example.techstoreapp.data.model.CartSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {

    @GET("api/cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<CartSummaryResponse>

    @POST("api/cart/items")
    suspend fun addItem(
        @Header("Authorization") token: String,
        @Body request: CartItemRequest
    ): Response<CartSummaryResponse>

    @PUT("api/cart/items/{itemId}")
    suspend fun updateItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Long,
        @Body request: CartItemRequest
    ): Response<CartSummaryResponse>

    @DELETE("api/cart/items/{itemId}")
    suspend fun removeItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Long
    ): Response<CartSummaryResponse>

    @DELETE("api/cart")
    suspend fun clearCart(
        @Header("Authorization") token: String
    ): Response<CartSummaryResponse>
}