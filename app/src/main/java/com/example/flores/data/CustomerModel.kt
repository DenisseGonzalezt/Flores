package com.example.flores.data

// Representa la información personal de un cliente.
data class CustomerModel(

    // Identificador único del cliente.
    val customerId: String = "",

    // Identificador de la cuenta con la que inicia sesión.
    val userId: String = "",

    val name: String = "",

    val email: String = "",

    val phone: String = "",

    val address: String = ""
)