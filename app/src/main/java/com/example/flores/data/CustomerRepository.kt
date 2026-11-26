package com.example.flores.data

import kotlinx.coroutines.flow.Flow

// Define las operaciones para trabajar con los clientes.
interface CustomerRepository {

    // Obtener todos los clientes.
    fun getCustomers(): Flow<List<CustomerModel>>

    // Buscar un cliente por su identificador.
    fun getCustomerById(customerId: String): Flow<CustomerModel?>

    // Buscar el cliente relacionado con una cuenta de usuario.
    fun getCustomerByUserId(userId: String): Flow<CustomerModel?>

    // Registrar un cliente.
    suspend fun insertCustomer(customer: CustomerModel)

    // Actualizar sus datos.
    suspend fun updateCustomer(customer: CustomerModel)

    // Eliminar un cliente.
    suspend fun deleteCustomer(customer: CustomerModel)
}