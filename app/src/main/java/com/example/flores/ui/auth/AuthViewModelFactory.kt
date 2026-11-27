package com.example.flores.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flores.data.AuthRepository

// Permite crear AuthViewModel utilizando el repositorio de autenticación.
class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    // Construye el ViewModel solicitado.
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
