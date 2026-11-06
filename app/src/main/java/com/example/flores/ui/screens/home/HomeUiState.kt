package com.example.flores.ui.screens.home

import com.example.flores.data.ItemModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val items: List<ItemModel> = emptyList(),
    val errorMessage: String? = null
)