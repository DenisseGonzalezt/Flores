package com.example.flores.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flores.data.AppContainer
import com.example.flores.data.ProductModel
import com.example.flores.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: ProductRepository = AppContainer.productRepository
) : ViewModel() {

    private val _showLowStockOnly = MutableStateFlow(false)
    private val _batchDialogProduct = MutableStateFlow<ProductModel?>(null)

    val uiState: StateFlow<InventoryUiState> = combine(
        repository.getProducts(),
        _showLowStockOnly,
        _batchDialogProduct
    ) { allProducts, showLowStockOnly, batchProduct ->
        val filtered = if (showLowStockOnly) {
            allProducts.filter { it.stock <= it.minStock }
        } else {
            allProducts
        }

        InventoryUiState(
            isLoading = false,
            products = filtered,
            showLowStockOnly = showLowStockOnly,
            totalUnits = allProducts.sumOf { it.stock },
            lowStockCount = allProducts.count { it.stock <= it.minStock },
            batchDialogProduct = batchProduct
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState(isLoading = true)
    )

    fun toggleLowStockFilter() {
        _showLowStockOnly.value = !_showLowStockOnly.value
    }

    fun openBatchDialog(product: ProductModel) {
        _batchDialogProduct.value = product
    }

    fun dismissBatchDialog() {
        _batchDialogProduct.value = null
    }

    fun confirmAddBatch(quantity: Int) {
        val targetProduct = _batchDialogProduct.value ?: return
        if (quantity > 0) {
            viewModelScope.launch {
                repository.addStockBatch(targetProduct.id, quantity)
            }
        }
        _batchDialogProduct.value = null
    }

    fun incrementStock(product: ProductModel) {
        viewModelScope.launch {
            repository.updateStock(product.id, product.stock + 1)
        }
    }

    fun decrementStock(product: ProductModel) {
        if (product.stock > 0) {
            viewModelScope.launch {
                repository.updateStock(product.id, product.stock - 1)
            }
        }
    }

    fun deleteProduct(product: ProductModel) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}