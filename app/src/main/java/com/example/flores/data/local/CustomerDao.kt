package com.example.flores.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Define las operaciones para acceder a la tabla de clientes.
@Dao
interface CustomerDao {

    // Obtiene todos los clientes registrados.
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    // Busca un cliente mediante su identificador.
    @Query("SELECT * FROM customers WHERE customerId = :id")
    fun getCustomerById(id: String): Flow<CustomerEntity?>

    // Busca el perfil de cliente asociado a una cuenta.
    @Query("SELECT * FROM customers WHERE userId = :userId LIMIT 1")
    fun getCustomerByUserId(userId: String): Flow<CustomerEntity?>

    // Registra un cliente o reemplaza sus datos si ya existe.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    // Actualiza los datos de un cliente.
    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    // Elimina un cliente de la base de datos local.
    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)
}