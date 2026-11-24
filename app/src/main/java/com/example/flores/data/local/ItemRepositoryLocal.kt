package com.example.flores.data.local

import com.example.flores.data.ItemModel
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepositoryLocal(private val itemDao: ItemDao) : ItemRepository {

    override fun getItems(): Flow<List<ItemModel>> {
        return itemDao.getAllItems().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getItemById(id: Int): Flow<ItemModel?> {
        return itemDao.getItemById(id).map { entity ->
            entity?.toModel()
        }
    }

    override fun getLowStockItems(): Flow<List<ItemModel>> {
        return itemDao.getLowStockItems().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun updateStock(id: Int, newStock: Int) {
        itemDao.updateStock(id, newStock.coerceAtLeast(0))
    }

    override suspend fun insertItem(item: ItemModel) {
        itemDao.insertItem(item.toEntity())
    }

    override suspend fun updateItem(item: ItemModel) {
        itemDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(item: ItemModel) {
        itemDao.deleteItem(item.toEntity())
    }
}