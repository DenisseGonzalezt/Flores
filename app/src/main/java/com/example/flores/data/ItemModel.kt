package com.example.flores.data

data class ItemModel(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isUrgent: Boolean = false,
    val category: String = "General",
    val time: String = "",
    val stock: Int = 0,
    val minStock: Int = 5,
    val price: Double = 0.0
)