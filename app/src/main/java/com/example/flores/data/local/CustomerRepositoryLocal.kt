package com.example.flores.data.local

import com.example.flores.data.CustomerModel
import com.example.flores.data.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementa las operaciones del repositorio usando Room.
class CustomerRepositoryLocal(
    private val customerDao: CustomerDao
) : CustomerRepository {

    // Obtiene todos los clientes y convierte sus entidades en modelos.
    override fun getCustomers(): Flow<List<CustomerModel>> {
        return customerDao.getAllCustomers().map { customers ->
            customers.map { it.toModel() }
        }
    }

    // Busca un cliente por su identificador.
    override fun getCustomerById(customerId: String): Flow<CustomerModel?> {
        return customerDao.getCustomerById(customerId).map { customer ->
            customer?.toModel()
        }
    }

    // Busca el cliente asociado a una cuenta.
    override fun getCustomerByUserId(userId: String): Flow<CustomerModel?> {
        return customerDao.getCustomerByUserId(userId).map { customer ->
            customer?.toModel()
        }
    }

    // Guarda un nuevo cliente en Room.
    override suspend fun insertCustomer(customer: CustomerModel) {
        customerDao.insertCustomer(customer.toEntity())
    }

    // Actualiza los datos de un cliente.
    override suspend fun updateCustomer(customer: CustomerModel) {
        customerDao.updateCustomer(customer.toEntity())
    }

    // Elimina un cliente de Room.
    override suspend fun deleteCustomer(customer: CustomerModel) {
        customerDao.deleteCustomer(customer.toEntity())
    }
}
