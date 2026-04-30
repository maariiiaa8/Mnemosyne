package com.mnemosyne.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class HomeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Obtiene todas las exposiciones destacadas de todos los museos
    suspend fun obtenerExposicionesDestacadas(): FirebaseResult<List<Exposicion>> {
        return try {
            val museos = db.collection("museos").get().await()
            Log.d("HOME", "Museos encontrados: ${museos.documents.size}")
            val exposiciones = mutableListOf<Exposicion>()

            for (museo in museos.documents) {
                val expos = db.collection("museos")
                    .document(museo.id)
                    .collection("exposiciones")
                    .whereEqualTo("destacada", true)
                    .get()
                    .await()
                Log.d("HOME", "Exposiciones en ${museo.id}: ${expos.documents.size}")

                expos.documents.forEach { doc ->
                    // Leemos fechaInicio y fechaFin manualmente para soportar Timestamp y String
                    val fechaInicio = parsearFecha(doc, "fechaInicio")
                    val fechaFin    = parsearFecha(doc, "fechaFin")

                    exposiciones.add(
                        Exposicion(
                            id          = doc.id,
                            titulo      = doc.getString("titulo") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            fechaInicio = fechaInicio,
                            fechaFin    = fechaFin,
                            imagenUrl   = doc.getString("imagenUrl") ?: "",
                            destacada   = doc.getBoolean("destacada") ?: false,
                            museoNombre = doc.getString("museoNombre") ?: ""
                        )
                    )
                }
            }
            FirebaseResult.Success(exposiciones)
        } catch (e: Exception) {
            Log.e("HOME", "Error: ${e.message}")
            FirebaseResult.Error(e.message ?: "Error al cargar exposiciones")
        }
    }

    // Obtiene todas las noticias destacadas de todos los museos
    suspend fun obtenerNoticiasDestacadas(): FirebaseResult<List<Noticia>> {
        return try {
            val museos = db.collection("museos").get().await()
            val noticias = mutableListOf<Noticia>()

            for (museo in museos.documents) {
                val news = db.collection("museos")
                    .document(museo.id)
                    .collection("noticias")
                    .whereEqualTo("destacada", true)
                    .get()
                    .await()

                news.documents.forEach { doc ->
                    val fecha = parsearFecha(doc, "fecha")

                    noticias.add(
                        Noticia(
                            id          = doc.id,
                            titulo      = doc.getString("titulo") ?: "",
                            contenido   = doc.getString("contenido") ?: "",
                            fecha       = fecha,
                            imagenUrl   = doc.getString("imagenUrl") ?: "",
                            destacada   = doc.getBoolean("destacada") ?: false,
                            museoNombre = doc.getString("museoNombre") ?: ""
                        )
                    )
                }
            }
            FirebaseResult.Success(noticias)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar noticias")
        }
    }

    // Convierte Timestamp o String a texto con formato dd/MM/yyyy
    private fun parsearFecha(doc: com.google.firebase.firestore.DocumentSnapshot, campo: String): String {
        return try {
            val timestamp = doc.getTimestamp(campo)
            if (timestamp != null) {
                formato.format(timestamp.toDate())
            } else {
                doc.getString(campo) ?: ""
            }
        } catch (e: Exception) {
            doc.getString(campo) ?: ""
        }
    }
}