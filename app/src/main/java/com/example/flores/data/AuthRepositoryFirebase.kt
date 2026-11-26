package com.example.flores.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Implementa el registro y acceso de usuarios mediante Firebase.
class AuthRepositoryFirebase(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    // Registra una cuenta nueva y crea su perfil de cliente.
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ): Result<UserModel> {

        return try {
            // Firebase Authentication crea la cuenta.
            val result = auth.createUserWithEmailAndPassword(
                email.trim(),
                password
            ).await()

            val firebaseUser = result.user
                ?: throw Exception("No se pudo crear el usuario.")

            // Todo registro público comienza con el rol CLIENTE.
            val user = UserModel(
                uid = firebaseUser.uid,
                name = name.trim(),
                email = email.trim(),
                role = UserModel.CLIENTE
            )

            // Guardamos el perfil general del usuario.
            firestore.collection("users")
                .document(user.uid)
                .set(
                    mapOf(
                        "uid" to user.uid,
                        "name" to user.name,
                        "email" to user.email,
                        "role" to user.role
                    )
                ).await()

            // Guardamos los datos adicionales del cliente.
            firestore.collection("customers")
                .document(user.uid)
                .set(
                    mapOf(
                        "customerId" to user.uid,
                        "userId" to user.uid,
                        "name" to user.name,
                        "email" to user.email,
                        "phone" to phone.trim(),
                        "address" to address.trim()
                    )
                ).await()

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Comprueba las credenciales e identifica el rol del usuario.
    override suspend fun login(
        email: String,
        password: String
    ): Result<UserModel> {

        return try {
            val result = auth.signInWithEmailAndPassword(
                email.trim(),
                password
            ).await()

            val firebaseUser = result.user
                ?: throw Exception("No se pudo iniciar sesión.")

            // Recuperamos el perfil guardado en Firestore.
            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró el perfil del usuario.")
            }

            val user = UserModel(
                uid = firebaseUser.uid,
                name = document.getString("name") ?: "",
                email = firebaseUser.email ?: "",
                role = document.getString("role") ?: UserModel.CLIENTE
            )

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Finaliza la sesión de Firebase.
    override fun logout() {
        auth.signOut()
    }

    // Recupera el usuario que tiene una sesión activa.
    override suspend fun getCurrentUser(): Result<UserModel?> {

        return try {
            val firebaseUser = auth.currentUser

            if (firebaseUser == null) {
                return Result.success(null)
            }

            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("No se encontró el perfil del usuario.")
            }

            val user = UserModel(
                uid = firebaseUser.uid,
                name = document.getString("name") ?: "",
                email = firebaseUser.email ?: "",
                role = document.getString("role") ?: UserModel.CLIENTE
            )

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
