package com.example.flores.data.network

import com.example.flores.data.ItemModel
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    override suspend fun insertItem(item: ItemModel) {
        try {
            val apiItem = ApiItem(id = item.id, title = item.title, body = item.description)
            apiService.insertItem(apiItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateItem(item: ItemModel) {
        try {
            val apiItem = ApiItem(id = item.id, title = item.title, body = item.description)
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