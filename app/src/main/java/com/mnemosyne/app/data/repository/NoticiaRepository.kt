package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class NoticiaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("noticias")

    suspend fun obtenerNoticias(): FirebaseResult<List<Noticia>> {
        return try {
            val snapshot = coleccion.get().await()
            val lista = snapshot.documents.map { doc ->
                doc.toObject(Noticia::class.java)!!.copy(id = doc.id)
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar noticias")
        }
    }

    suspend fun crearNoticia(noticia: Noticia): FirebaseResult<Unit> {
        return try {
            coleccion.add(noticia).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear noticia")
        }
    }
}