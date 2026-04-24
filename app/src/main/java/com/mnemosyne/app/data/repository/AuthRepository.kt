package com.mnemosyne.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import com.mnemosyne.app.utils.FirebaseResult

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

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
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build()
            result.user!!.updateProfile(profileUpdates).await()
            FirebaseResult.Success(result.user!!)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al registrarse")
        }
    }

    fun usuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    fun cerrarSesion() = auth.signOut()
}