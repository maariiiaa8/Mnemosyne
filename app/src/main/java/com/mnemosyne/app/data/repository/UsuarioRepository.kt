package com.mnemosyne.app.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mnemosyne.app.data.model.Usuario
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val uid get() = auth.currentUser?.uid

    suspend fun obtenerPerfil(): FirebaseResult<Usuario> {
        return try {
            val uid = uid ?: return FirebaseResult.Error("No hay usuario logueado")
            val ref = db.collection("usuarios").document(uid)
            val doc = ref.get().await()

            if (!doc.exists()) {
                val authUser = auth.currentUser!!
                val datosUsuario = hashMapOf(
                    "nombre"        to (authUser.displayName ?: ""),
                    "email"         to (authUser.email ?: ""),
                    "fotoPerfil"    to "",
                    "fechaRegistro" to Timestamp.now(),
                    "favoritos"     to emptyList<String>()
                )
                ref.set(datosUsuario).await()
                return obtenerPerfil()
            }

            FirebaseResult.Success(
                Usuario(
                    uid           = uid,
                    nombre        = doc.getString("nombre") ?: "",
                    email         = doc.getString("email") ?: "",
                    fotoPerfil    = doc.getString("fotoPerfil") ?: "",
                    favoritos     = (doc.get("favoritos") as? List<String>) ?: emptyList(),
                    fechaRegistro = doc.getTimestamp("fechaRegistro")
                        ?.toDate()
                        ?.let {
                            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                .format(it)
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
            db.collection("usuarios").document(uid)
                .update("nombre", nuevoNombre)
                .await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar el nombre")
        }
    }

    // privada — solo la usa subirFotoPerfil
    private suspend fun actualizarFoto(url: String): FirebaseResult<Unit> {
        return try {
            val uid = uid ?: return FirebaseResult.Error("No hay usuario logueado")
            db.collection("usuarios").document(uid)
                .update("fotoPerfil", url)
                .await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar la foto")
        }
    }

    suspend fun subirFotoPerfil(uri: Uri): FirebaseResult<String> {
        return try {
            val uid = uid ?: return FirebaseResult.Error("No hay usuario logueado")
            val ref = storage.reference.child("fotos_perfil/$uid.jpg")
            ref.putFile(uri).await<com.google.firebase.storage.UploadTask.TaskSnapshot>()
            val url = ref.downloadUrl.await<android.net.Uri>().toString()
            actualizarFoto(url)
            FirebaseResult.Success(url)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al subir la foto")
        }
    }
}