package com.example.flores.data

import android.content.Context
import com.example.flores.data.local.AppDatabase
import com.example.flores.data.local.ItemRepositoryLocal
import com.example.flores.data.local.ProductRepositoryLocal
import com.example.flores.data.local.CustomerRepositoryLocal
import com.example.flores.data.network.ApiService
import com.example.flores.data.network.ItemRepositoryNetwork
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {

    // 1. Repositorios de Firebase Firestore (Remote Cloud).
    val itemRepository: ItemRepository by lazy {
        ItemRepositoryFirebase()
    }

    val productRepository: ProductRepository by lazy {
        ProductRepositoryFirebase()
    }

    // Repositorio para registrar usuarios e iniciar sesión con Firebase.
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase()
    }

    // 2. Instancia del repositorio de Retrofit Web API (Network).
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

    // 3. Instancias de los repositorios locales de Room (SQLite).
    fun getLocalRepository(context: Context): ItemRepository {
        val database = AppDatabase.getDatabase(context)
        return ItemRepositoryLocal(database.itemDao())
    }

    fun getLocalProductRepository(context: Context): ProductRepository {
        val database = AppDatabase.getDatabase(context)
        return ProductRepositoryLocal(database.productDao())
    }

    // 4. Repositorio local para administrar los clientes.
    fun getLocalCustomerRepository(context: Context): CustomerRepository {
        val database = AppDatabase.getDatabase(context)
        return CustomerRepositoryLocal(database.customerDao())
    }
}