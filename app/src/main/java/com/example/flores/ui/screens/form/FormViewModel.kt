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
                val newItem = ItemModel(
                    id = 0,
                    title = currentState.titleInput,
                    description = currentState.descriptionInput,
                    isUrgent = currentState.isUrgent,
                    category = currentState.selectedCategory.ifEmpty { "General" },
                    time = currentState.selectedTime.ifEmpty { "12:00 PM" }
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