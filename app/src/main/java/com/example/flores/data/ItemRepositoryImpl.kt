package com.example.flores.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ItemRepositoryImpl : ItemRepository {

    private val itemsFlow = MutableStateFlow<List<ItemModel>>(
        listOf(
            ItemModel(
                id = 1,
                title = "Pedido #1 - Ramo de rosas",
                description = "12 rosas rojas con tarjeta personalizada.",
                isUrgent = false,
                category = "General",
                time = "08:00 AM"
            ),
            ItemModel(
                id = 2,
                title = "Pedido #2 - Arreglo de orquídea",
                description = "Orquídea blanca en maceta de cerámica.",
                isUrgent = true,
                category = "Personal",
                time = "10:30 AM"
            ),
            ItemModel(
                id = 3,
                title = "Pedido #3 - Centro de mesa",
                description = "Girasoles y follaje verde para evento.",
                isUrgent = false,
                category = "Trabajo",
                time = "02:15 PM"
            )
        )
    )

    override fun getItems(): Flow<List<ItemModel>> {
        return itemsFlow.asStateFlow()
    }

    override fun getItemById(id: Int): Flow<ItemModel?> {
        return itemsFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun insertItem(item: ItemModel) {
        val currentList = itemsFlow.value.toMutableList()
        val newItem = item.copy(id = (currentList.maxOfOrNull { it.id } ?: 0) + 1)
        currentList.add(newItem)
        itemsFlow.value = currentList
    }

    override suspend fun updateItem(item: ItemModel) {
        val currentList = itemsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentList[index] = item
            itemsFlow.value = currentList
        }
    }

    override suspend fun deleteItem(item: ItemModel) {
        val currentList = itemsFlow.value.toMutableList()
        currentList.removeAll { it.id == item.id }
        itemsFlow.value = currentList
    }
}