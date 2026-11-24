package com.example.flores.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class ProductRepositoryFirebase : ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    override fun getProducts(): Flow<List<ProductModel>> = callbackFlow {
        val listener = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    ProductModel(
                        id = doc.getLong("id")?.toInt() ?: doc.id.hashCode(),
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        category = doc.getString("category") ?: "Ramos",
                        price = doc.getDouble("price") ?: 0.0,
                        stock = doc.getLong("stock")?.toInt() ?: 0,
                        minStock = doc.getLong("minStock")?.toInt() ?: 5
                    )
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getProductById(id: Int): Flow<ProductModel?> = callbackFlow {
        val listener = productsCollection.whereEqualTo("id", id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val doc = snapshot?.documents?.firstOrNull()
                val product = doc?.let {
                    ProductModel(
                        id = it.getLong("id")?.toInt() ?: id,
                        name = it.getString("name") ?: "",
                        description = it.getString("description") ?: "",
                        category = it.getString("category") ?: "Ramos",
                        price = it.getDouble("price") ?: 0.0,
                        stock = it.getLong("stock")?.toInt() ?: 0,
                        minStock = it.getLong("minStock")?.toInt() ?: 5
                    )
                }
                trySend(product)
            }
        awaitClose { listener.remove() }
    }

    override fun getLowStockProducts(): Flow<List<ProductModel>> {
        return getProducts().map { list ->
            list.filter { it.stock <= it.minStock }
        }
    }

    override suspend fun updateStock(id: Int, newStock: Int) {
        productsCollection.document(id.toString())
            .update("stock", newStock.coerceAtLeast(0))
    }

    override suspend fun addStockBatch(id: Int, quantity: Int) {
        if (quantity > 0) {
            productsCollection.document(id.toString())
                .update("stock", FieldValue.increment(quantity.toLong()))
        }
    }

    override suspend fun insertProduct(product: ProductModel) {
        val newId = if (product.id != 0) product.id else System.currentTimeMillis().toInt()
        val data = hashMapOf(
            "id" to newId,
            "name" to product.name,
            "description" to product.description,
            "category" to product.category,
            "price" to product.price,
            "stock" to product.stock,
            "minStock" to product.minStock
        )
        productsCollection.document(newId.toString()).set(data)
    }

    override suspend fun updateProduct(product: ProductModel) {
        val data = hashMapOf(
            "id" to product.id,
            "name" to product.name,
            "description" to product.description,
            "category" to product.category,
            "price" to product.price,
            "stock" to product.stock,
            "minStock" to product.minStock
        )
        productsCollection.document(product.id.toString()).set(data)
    }

    override suspend fun deleteProduct(product: ProductModel) {
        productsCollection.document(product.id.toString()).delete()
    }
}