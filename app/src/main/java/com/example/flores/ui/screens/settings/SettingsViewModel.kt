package com.example.flores.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.flores.data.ThemeAndLocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isDarkMode = ThemeAndLocaleManager.isDarkMode.value,
            selectedLanguage = if (ThemeAndLocaleManager.currentLanguage.value == "es") "Español" else "English"
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onStoreNameChanged(newName: String) {
        _uiState.value = _uiState.value.copy(storeName = newName)
    }

    fun onDarkModeChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = enabled)
        ThemeAndLocaleManager.setDarkMode(enabled)
    }

    fun onSyncWithCloudChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(syncWithCloud = enabled)
    }

    fun onLanguageSelected(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        val code = if (language == "Español") "es" else "en"
        ThemeAndLocaleManager.setLanguage(code)
    }

    fun onClosingTimeChanged(time: String) {
        _uiState.value = _uiState.value.copy(closingTime = time)
    }
}