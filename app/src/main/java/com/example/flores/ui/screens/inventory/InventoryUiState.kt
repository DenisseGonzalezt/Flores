package com.example.flores.ui.screens.inventory

import com.example.flores.data.ProductModel

data class InventoryUiState(
    val isLoading: Boolean = false,
    val products: List<ProductModel> = emptyList(),
    val showLowStockOnly: Boolean = false,
    val totalUnits: Int = 0,
    val lowStockCount: Int = 0,
    val batchDialogProduct: ProductModel? = null
)