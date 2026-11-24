package com.example.flores.ui.screens.form

data class FormUiState(
    val titleInput: String = "",
    val descriptionInput: String = "",
    val isUrgent: Boolean = false,
    val selectedCategory: String = "General",
    val selectedTime: String = "12:00 PM",
    val isSaving: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)