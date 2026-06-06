package com.example.techstoreapp.data.remote

import com.example.techstoreapp.data.model.CategoryResponse
import com.example.techstoreapp.data.model.ProductRequest
import com.example.techstoreapp.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/products/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<List<CategoryResponse>>

    @GET("api/products")
    suspend fun getProducts(
        @Header("Authorization") token: String
    ): Response<List<ProductResponse>>

    @GET("api/products")
    suspend fun getProductsByCategory(
        @Header("Authorization") token: String,
        @Query("categoryId") categoryId: Long
    ): Response<List<ProductResponse>>

    @POST("api/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Long,
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Long
    ): Response<Unit>
}