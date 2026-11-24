package com.example.flores.data

data class ProductModel(
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String = "Ramos",
    val price: Double = 0.0,
    val stock: Int = 0,
    val minStock: Int = 5
)