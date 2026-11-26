package com.example.flores.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Incluye las tablas de pedidos, productos y clientes.
@Database(
    entities = [
        ItemEntity::class,
        ProductEntity::class,
        CustomerEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Acceso a los pedidos.
    abstract fun itemDao(): ItemDao

    // Acceso a los productos.
    abstract fun productDao(): ProductDao

    // Acceso a los clientes.
    abstract fun customerDao(): CustomerDao

    companion object {

        @Volatile
        private var Instance: AppDatabase? = null

        // Agrega la tabla de clientes sin eliminar las tablas anteriores.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS customers (
                        customerId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        address TEXT NOT NULL,
                        PRIMARY KEY(customerId)
                    )
                    """.trimIndent()
                )
            }
        }

        // Obtiene una única instancia de la base de datos.
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flores_database"
                )
                    .addMigrations(MIGRATION_3_4)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}