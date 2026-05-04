package com.mnemosyne.app.data.repository

<<<<<<< HEAD
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.mnemosyne.app.data.model.Usuario
import kotlinx.coroutines.tasks.await
=======
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
>>>>>>> MNE-PASARELA_DE_PAGO
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): FirebaseResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            FirebaseResult.Success(result.user!!)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun registro(nombre: String, email: String, password: String): FirebaseResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // Actualizar displayName en Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            user.updateProfile(profileUpdates).await()

            // Crear documento en Firestore
            val datosUsuario = hashMapOf(
                "nombre"        to nombre,
                "email"         to email,
                "fotoPerfil"    to "",
                "fechaRegistro" to Timestamp.now()
            )
            db.collection("usuarios")
                .document(user.uid)
                .set(datosUsuario)
                .await()

            FirebaseResult.Success(user)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al registrarse")
        }
    }


    fun usuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    // En AuthRepository.kt
    suspend fun obtenerDatosUsuario(uid: String): FirebaseResult<Usuario> {
        return try {
            val snapshot = Firebase.firestore.collection("usuarios").document(uid).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            if (usuario != null) {
                FirebaseResult.Success(usuario)
            } else {
                FirebaseResult.Error("Usuario no encontrado en la base de datos")
            }
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al obtener datos")
        }
    }

    fun cerrarSesion() = auth.signOut()
}