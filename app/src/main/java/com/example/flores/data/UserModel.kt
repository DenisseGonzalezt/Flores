package com.example.flores.data

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "CLIENTE"
) {
    companion object {
        const val ADMINISTRADOR = "ADMINISTRADOR"
        const val CLIENTE = "CLIENTE"
    }
}
