package com.example.flores.data

import android.content.Context
import com.example.flores.data.local.AppDatabase
import com.example.flores.data.local.ItemRepositoryLocal
import com.example.flores.data.network.ApiService
import com.example.flores.data.network.ItemRepositoryNetwork
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {

    // 1. Instancia del Repositorio de Firebase Firestore (Remote Cloud)
    val itemRepository: ItemRepository by lazy {
        ItemRepositoryFirebase()
    }

    // 2. Instancia del Repositorio de Retrofit Web API (Network)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val networkRepository: ItemRepository by lazy {
        ItemRepositoryNetwork(apiService)
    }

    // 3. Instancia del Repositorio Local de Room (SQLite)
    fun getLocalRepository(context: Context): ItemRepository {
        val database = AppDatabase.getDatabase(context)
        return ItemRepositoryLocal(database.itemDao())
    }
}