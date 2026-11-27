package com.example.flores.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flores.data.AuthRepository
import com.example.flores.data.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar el registro y el inicio de sesión.
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Guarda los datos del usuario que inició sesión.
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    // Indica si se está realizando alguna operación.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Indica si ya se comprobó la sesión al abrir la aplicación.
    private val _sessionChecked = MutableStateFlow(false)
    val sessionChecked: StateFlow<Boolean> = _sessionChecked.asStateFlow()

    // Guarda mensajes de éxito o error.
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // Registra un nuevo cliente en Firebase.
    fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null

            try {
                val result = authRepository.register(
                    name, email, password, phone, address
                )

                result.onSuccess { registeredUser ->
                    _user.value = registeredUser
                    _message.value = "Registro exitoso."
                }.onFailure { error ->
                    _message.value =
                        error.message ?: "No se pudo completar el registro."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Inicia sesión con correo y contraseña.
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null

            try {
                val result = authRepository.login(email, password)

                result.onSuccess { loggedUser ->
                    _user.value = loggedUser
                    _message.value = "Bienvenido, ${loggedUser.name}."
                }.onFailure { error ->
                    _message.value =
                        error.message ?: "No se pudo iniciar sesión."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Recupera la sesión activa de Firebase.
    fun checkSession() {
        if (_sessionChecked.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = authRepository.getCurrentUser()

                result.onSuccess { currentUser ->
                    _user.value = currentUser
                }.onFailure { error ->
                    _message.value =
                        error.message ?: "No se pudo recuperar la sesión."
                }
            } finally {
                _isLoading.value = false
                _sessionChecked.value = true
            }
        }
    }

    // Cierra la sesión del usuario.
    fun logout() {
        authRepository.logout()
        _user.value = null
        _message.value = "Sesión cerrada."
    }

    // Limpia los mensajes mostrados.
    fun clearMessage() {
        _message.value = null
    }
}