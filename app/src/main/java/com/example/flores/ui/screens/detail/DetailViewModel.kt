package com.example.flores.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flores.data.AppContainer
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: ItemRepository = AppContainer.itemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadItemDetail(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)
            repository.getItemById(itemId).collect { item ->
                _uiState.value = DetailUiState(
                    item = item,
                    isLoading = false
                )
            }
        }
    }

    fun incrementStock() {
        val currentItem = _uiState.value.item ?: return
        viewModelScope.launch {
            repository.updateStock(currentItem.id, currentItem.stock + 1)
        }
    }

    fun decrementStock() {
        val currentItem = _uiState.value.item ?: return
        if (currentItem.stock > 0) {
            viewModelScope.launch {
                repository.updateStock(currentItem.id, currentItem.stock - 1)
            }
        }
    }
}