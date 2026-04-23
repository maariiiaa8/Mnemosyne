package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class ExposicionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("exposiciones")

    suspend fun obtenerExposiciones(): FirebaseResult<List<Exposicion>> {
        return try {
            val snapshot = coleccion.get().await()
            val lista = snapshot.documents.map { doc ->
                doc.toObject(Exposicion::class.java)!!.copy(id = doc.id)
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar exposiciones")
        }
    }

    suspend fun crearExposicion(exposicion: Exposicion): FirebaseResult<Unit> {
        return try {
            coleccion.add(exposicion).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear exposición")
        }
    }
}