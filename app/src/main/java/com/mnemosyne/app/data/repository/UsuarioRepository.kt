package com.mnemosyne.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Usuario
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val uid get() = auth.currentUser?.uid

    suspend fun obtenerPerfil(): FirebaseResult<Usuario> {
        return try {
            val uid = uid ?: return FirebaseResult.Error("No hay usuario logueado")

            val doc = db.collection("usuarios")
                .document(uid)
                .get()
                .await()

            if (!doc.exists()) {
                val user = auth.currentUser!!
                val data = hashMapOf(
                    "nombre" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "fotoPerfil" to "",
                    "fechaRegistro" to Timestamp.now(),
                    "favoritos" to emptyList<String>()
                )

                db.collection("usuarios")
                    .document(uid)
                    .set(data)
                    .await()

                return obtenerPerfil()
            }

            FirebaseResult.Success(
                Usuario(
                    uid = uid,
                    nombre = doc.getString("nombre") ?: "",
                    email = doc.getString("email") ?: "",
                    fotoPerfil = doc.getString("fotoPerfil") ?: "",
                    favoritos = (doc.get("favoritos") as? List<String>) ?: emptyList(),
                    fechaRegistro = doc.getTimestamp("fechaRegistro")
                        ?.toDate()
                        ?.let {
                            java.text.SimpleDateFormat(
                                "dd/MM/yyyy",
                                java.util.Locale.getDefault()
                            ).format(it)
                        } ?: ""
                )
            )

        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar el perfil")
        }
    }

    suspend fun actualizarNombre(nuevoNombre: String): FirebaseResult<Unit> {
        return try {
            val uid = uid ?: return FirebaseResult.Error("No hay usuario logueado")

            db.collection("usuarios")
                .document(uid)
                .update("nombre", nuevoNombre)
                .await()

            FirebaseResult.Success(Unit)

        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar el nombre")
        }
    }
}