package com.example.flores.data

// Define las operaciones de autenticación de usuarios.
interface AuthRepository {

    // Registrar una cuenta nueva de cliente.
    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ): Result<UserModel>

    // Iniciar sesión con correo y contraseña.
    suspend fun login(
        email: String,
        password: String
    ): Result<UserModel>

    // Cerrar la sesión actual.
    fun logout()

    // Consultar el usuario que tiene una sesión activa.
    suspend fun getCurrentUser(): Result<UserModel?>
}
