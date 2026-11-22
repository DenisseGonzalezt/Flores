package com.example.flores.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flores.data.AppContainer
import com.example.flores.data.ItemModel
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ItemRepository = AppContainer.itemRepository
) : ViewModel() {

    private val _showLowStockOnly = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getItems(),
        _showLowStockOnly
    ) { allItems, showLowStockOnly ->
        val filteredItems = if (showLowStockOnly) {
            allItems.filter { it.stock <= it.minStock }
        } else {
            allItems
        }

        HomeUiState(
            isLoading = false,
            items = filteredItems,
            showLowStockOnly = showLowStockOnly,
            totalUnits = allItems.sumOf { it.stock },
            lowStockCount = allItems.count { it.stock <= it.minStock }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun toggleLowStockFilter() {
        _showLowStockOnly.value = !_showLowStockOnly.value
    }

    fun incrementStock(item: ItemModel) {
        viewModelScope.launch {
            repository.updateStock(item.id, item.stock + 1)
        }
    }

    fun decrementStock(item: ItemModel) {
        if (item.stock > 0) {
            viewModelScope.launch {
                repository.updateStock(item.id, item.stock - 1)
            }
        }
    }

    fun deleteItem(item: ItemModel) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
}