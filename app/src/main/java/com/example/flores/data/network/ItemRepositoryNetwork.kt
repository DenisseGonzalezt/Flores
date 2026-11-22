package com.example.flores.data.network

import com.example.flores.data.ItemModel
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ItemRepositoryNetwork(private val apiService: ApiService) : ItemRepository {

    override fun getItems(): Flow<List<ItemModel>> = flow {
        try {
            val apiItems = apiService.getItems()
            emit(apiItems.map { it.toModel() })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getItemById(id: Int): Flow<ItemModel?> = flow {
        try {
            val apiItem = apiService.getItemById(id)
            emit(apiItem.toModel())
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getLowStockItems(): Flow<List<ItemModel>> {
        return getItems().map { list ->
            list.filter { it.stock <= it.minStock }
        }
    }

    override suspend fun updateStock(id: Int, newStock: Int) {
        try {
            val currentItem = apiService.getItemById(id)
            val updatedItem = currentItem.copy(stock = newStock.coerceAtLeast(0))
            apiService.updateItem(id, updatedItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertItem(item: ItemModel) {
        try {
            val apiItem = ApiItem(
                id = item.id,
                title = item.title,
                body = item.description,
                stock = item.stock,
                minStock = item.minStock,
                price = item.price
            )
            apiService.insertItem(apiItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateItem(item: ItemModel) {
        try {
            val apiItem = ApiItem(
                id = item.id,
                title = item.title,
                body = item.description,
                stock = item.stock,
                minStock = item.minStock,
                price = item.price
            )
            apiService.updateItem(item.id, apiItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteItem(item: ItemModel) {
        try {
            apiService.deleteItem(item.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}