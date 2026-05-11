package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class ObraRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerObrasDeMuseo(museoId: String): FirebaseResult<List<Obra>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("obras")
                .get()
                .await()
            val obras = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Obra::class.java)?.copy(id = doc.id)
            }
            FirebaseResult.Success(obras)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar las obras")
        }
    }
}