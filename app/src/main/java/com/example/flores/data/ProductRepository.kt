package com.example.flores.data

import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductModel>>
    fun getProductById(id: Int): Flow<ProductModel?>
    fun getLowStockProducts(): Flow<List<ProductModel>>
    suspend fun updateStock(id: Int, newStock: Int)
    suspend fun addStockBatch(id: Int, quantity: Int)
    suspend fun insertProduct(product: ProductModel)
    suspend fun updateProduct(product: ProductModel)
    suspend fun deleteProduct(product: ProductModel)
}