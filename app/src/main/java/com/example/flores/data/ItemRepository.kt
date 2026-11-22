package com.example.flores.data

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItems(): Flow<List<ItemModel>>
    fun getItemById(id: Int): Flow<ItemModel?>
    fun getLowStockItems(): Flow<List<ItemModel>>
    suspend fun updateStock(id: Int, newStock: Int)
    suspend fun insertItem(item: ItemModel)
    suspend fun updateItem(item: ItemModel)
    suspend fun deleteItem(item: ItemModel)
}