package com.example.flores.ui.screens.detail

import com.example.flores.data.ItemModel

data class DetailUiState(
    val isLoading: Boolean = false,
    val item: ItemModel? = null,
    val errorMessage: String? = null
)