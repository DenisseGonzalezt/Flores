package com.example.flores.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flores.data.ProductModel

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val minStock: Int
)

fun ProductEntity.toModel(): ProductModel {
    return ProductModel(
        id = id,
        name = name,
        description = description,
        category = category,
        price = price,
        stock = stock,
        minStock = minStock
    )
}

fun ProductModel.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        category = category,
        price = price,
        stock = stock,
        minStock = minStock
    )
}