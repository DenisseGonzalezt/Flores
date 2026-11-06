package com.example.flores.ui.screens.settings

data class SettingsUiState(
    val storeName: String = "Florería Central",
    val isDarkMode: Boolean = false,
    val syncWithCloud: Boolean = true,
    val selectedLanguage: String = "Español",
    val closingTime: String = "20:00"
)