package com.example.flores.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ItemRepositoryFirebase : ItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")

    override fun getItems(): Flow<List<ItemModel>> = callbackFlow {
        val listener = itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val itemsList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: doc.id.hashCode()
                    val title = doc.getString("title") ?: ""
                    val description = doc.getString("description") ?: ""
                    val isUrgent = doc.getBoolean("urgent") ?: false
                    val category = doc.getString("category") ?: ""
                    val time = doc.getString("time") ?: ""

                    ItemModel(
                        id = id,
                        title = title,
                        description = description,
                        isUrgent = isUrgent,
                        category = category,
                        time = time
                    )
                }
                trySend(itemsList)
            }
        }

        awaitClose { listener.remove() }
    }

    override fun getItemById(id: Int): Flow<ItemModel?> = callbackFlow {
        val listener = itemsCollection.whereEqualTo("id", id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val doc = snapshot?.documents?.firstOrNull()
                val item = doc?.let {
                    ItemModel(
                        id = it.getLong("id")?.toInt() ?: id,
                        title = it.getString("title") ?: "",
                        description = it.getString("description") ?: "",
                        isUrgent = it.getBoolean("urgent") ?: false,
                        category = it.getString("category") ?: "",
                        time = it.getString("time") ?: ""
                    )
                }
                trySend(item)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun insertItem(item: ItemModel) {
        val newId = if (item.id != 0) item.id else System.currentTimeMillis().toInt()
        val itemData = hashMapOf(
            "id" to newId,
            "title" to item.title,
            "description" to item.description,
            "urgent" to item.isUrgent,
            "category" to item.category,
            "time" to item.time
        )
        // Guardado directo sin bloqueo de corrutina (.await)
        itemsCollection.document(newId.toString()).set(itemData)
    }

    override suspend fun updateItem(item: ItemModel) {
        val itemData = hashMapOf(
            "id" to item.id,
            "title" to item.title,
            "description" to item.description,
            "urgent" to item.isUrgent,
            "category" to item.category,
            "time" to item.time
        )
        itemsCollection.document(item.id.toString()).set(itemData)
    }

    override suspend fun deleteItem(item: ItemModel) {
        itemsCollection.document(item.id.toString()).delete()
    }
}