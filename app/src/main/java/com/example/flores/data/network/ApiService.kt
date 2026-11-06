package com.example.flores.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("posts")
    suspend fun getItems(): List<ApiItem>

    @GET("posts/{id}")
    suspend fun getItemById(@Path("id") id: Int): ApiItem

    @POST("posts")
    suspend fun insertItem(@Body item: ApiItem): ApiItem

    @PUT("posts/{id}")
    suspend fun updateItem(@Path("id") id: Int, @Body item: ApiItem): ApiItem

    @DELETE("posts/{id}")
    suspend fun deleteItem(@Path("id") id: Int): Response<Unit>
}