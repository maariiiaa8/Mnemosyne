package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class MuseoRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerMuseos(): FirebaseResult<List<Museo>> {
        return try {
            val snapshot = db.collection("museos").get().await()
            val museos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Museo::class.java)?.copy(id = doc.id)
            }
            FirebaseResult.Success(museos)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar los museos")
        }
    }
}