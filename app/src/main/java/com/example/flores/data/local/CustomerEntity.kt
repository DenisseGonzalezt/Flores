package com.example.flores.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flores.data.CustomerModel

// Define la tabla de clientes dentro de SQLite.
@Entity(tableName = "customers")
data class CustomerEntity(

    // Identificador único del cliente y sus datos
    @PrimaryKey
    val customerId: String,

    // Relaciona al cliente con su cuenta de usuario.
    val userId: String,

    val name: String,

    val email: String,

    val phone: String,

    val address: String
)

// Convierte un registro de Room al modelo utilizado por la aplicación.
fun CustomerEntity.toModel(): CustomerModel {
    return CustomerModel(
        customerId = customerId,
        userId = userId,
        name = name,
        email = email,
        phone = phone,
        address = address
    )
}

// Convierte el modelo de cliente en una entidad para guardar en Room.
fun CustomerModel.toEntity(): CustomerEntity {
    return CustomerEntity(
        customerId = customerId,
        userId = userId,
        name = name,
        email = email,
        phone = phone,
        address = address
    )
}