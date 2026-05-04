package com.mnemosyne.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class NoticiaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    suspend fun obtenerNoticias(): FirebaseResult<List<Noticia>> {
        return try {
            val museos = db.collection("museos").get().await()
            Log.d("NoticiaRepo", "Museos encontrados: ${museos.documents.size}")
            val noticias = mutableListOf<Noticia>()

            for (museo in museos.documents) {
                Log.d("NoticiaRepo", "Procesando museo: ${museo.id}")
                val snapshot = db.collection("museos")
                    .document(museo.id)
                    .collection("noticias")
                    .get()
                    .await()

                Log.d("NoticiaRepo", "Noticias en ${museo.id}: ${snapshot.documents.size}")

                for (doc in snapshot.documents) {
                    try {
                        Log.d("NoticiaRepo", "Doc ${doc.id} datos: ${doc.data}")
                        noticias.add(
                            Noticia(
                                id          = doc.id,
                                titulo      = doc.getString("titulo") ?: "",
                                contenido   = doc.getString("contenido") ?: "",
                                fecha       = parsearFecha(doc, "fecha"),
                                imagenUrl   = doc.getString("imagenUrl") ?: "",
                                destacada   = doc.getBoolean("destacada") ?: false,
                                museoNombre = doc.getString("museoNombre") ?: ""
                            )
                        )
                        Log.d("NoticiaRepo", "Doc ${doc.id} OK")
                    } catch (e: Exception) {
                        Log.e("NoticiaRepo", "Error en doc ${doc.id}: ${e.message}", e)
                    }
                }
            }

            Log.d("NoticiaRepo", "Total noticias cargadas: ${noticias.size}")
            FirebaseResult.Success(noticias)
        } catch (e: Exception) {
            Log.e("NoticiaRepo", "Error general: ${e.message}", e)
            FirebaseResult.Error(e.message ?: "Error al cargar noticias")
        }
    }

    suspend fun crearNoticia(museoId: String, noticia: Noticia): FirebaseResult<Unit> {
        return try {
            db.collection("museos")
                .document(museoId)
                .collection("noticias")
                .add(noticia)
                .await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al crear noticia")
        }
    }

    private fun parsearFecha(
        doc: com.google.firebase.firestore.DocumentSnapshot,
        campo: String
    ): String {
        return try {
            val timestamp = doc.getTimestamp(campo)
            if (timestamp != null) {
                formato.format(timestamp.toDate())
            } else {
                doc.getString(campo) ?: ""
            }
        } catch (e: Exception) {
            Log.e("NoticiaRepo", "Error parseando fecha campo '$campo': ${e.message}")
            doc.getString(campo) ?: ""
        }
    }
}