package com.example.flores.data.local

import com.example.flores.data.ProductModel
import com.example.flores.data.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryLocal(private val productDao: ProductDao) : ProductRepository {

    override fun getProducts(): Flow<List<ProductModel>> {
        return productDao.getAllProducts().map { list -> list.map { it.toModel() } }
    }

    override fun getProductById(id: Int): Flow<ProductModel?> {
        return productDao.getProductById(id).map { it?.toModel() }
    }

    override fun getLowStockProducts(): Flow<List<ProductModel>> {
        return productDao.getLowStockProducts().map { list -> list.map { it.toModel() } }
    }

    override suspend fun updateStock(id: Int, newStock: Int) {
        productDao.updateStock(id, newStock.coerceAtLeast(0))
    }

    override suspend fun addStockBatch(id: Int, quantity: Int) {
        if (quantity > 0) {
            productDao.addStockBatch(id, quantity)
        }
    }

    override suspend fun insertProduct(product: ProductModel) {
        productDao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: ProductModel) {
        productDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: ProductModel) {
        productDao.deleteProduct(product.toEntity())
    }
}