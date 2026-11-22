package com.example.flores.ui.screens.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flores.data.AppContainer
import com.example.flores.data.ItemModel
import com.example.flores.data.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormViewModel(
    private val repository: ItemRepository = AppContainer.itemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(titleInput = title)
    }

    fun onDescriptionChanged(description: String) {
        _uiState.value = _uiState.value.copy(descriptionInput = description)
    }

    fun onStockChanged(stock: String) {
        if (stock.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(stockInput = stock)
        }
    }

    fun onMinStockChanged(minStock: String) {
        if (minStock.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(minStockInput = minStock)
        }
    }

    fun onPriceChanged(price: String) {
        if (price.count { it == '.' } <= 1 && price.all { it.isDigit() || it == '.' }) {
            _uiState.value = _uiState.value.copy(priceInput = price)
        }
    }

    fun onUrgentChanged(isUrgent: Boolean) {
        _uiState.value = _uiState.value.copy(isUrgent = isUrgent)
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onTimeSelected(time: String) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }

    fun saveItem(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.titleInput.isBlank()) return

        _uiState.value = currentState.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val parsedStock = currentState.stockInput.toIntOrNull() ?: 0
                val parsedMinStock = currentState.minStockInput.toIntOrNull() ?: 5
                val parsedPrice = currentState.priceInput.toDoubleOrNull() ?: 0.0

                val newItem = ItemModel(
                    id = 0,
                    title = currentState.titleInput,
                    description = currentState.descriptionInput,
                    isUrgent = currentState.isUrgent,
                    category = currentState.selectedCategory.ifEmpty { "General" },
                    time = currentState.selectedTime.ifEmpty { "12:00 PM" },
                    stock = parsedStock,
                    minStock = parsedMinStock,
                    price = parsedPrice
                )
                repository.insertItem(newItem)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            }
        }
    }
}