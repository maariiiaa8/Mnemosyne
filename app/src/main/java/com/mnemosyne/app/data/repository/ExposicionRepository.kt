package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.TipoEntrada
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ExposicionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Obtiene todas las exposiciones de todos los museos con sus tipos de entrada
    suspend fun obtenerTodasExposiciones(): FirebaseResult<List<Exposicion>> {
        return try {
            val museos = db.collection("museos").get().await()
            val exposiciones = mutableListOf<Exposicion>()

            for (museo in museos.documents) {
                val expos = db.collection("museos")
                    .document(museo.id)
                    .collection("exposicion")
                    .get()
                    .await()

                for (doc in expos.documents) {
                    // Cargamos los tipos de entrada de cada exposición
                    val tiposSnapshot = db.collection("museos")
                        .document(museo.id)
                        .collection("exposicion")
                        .document(doc.id)
                        .collection("tiposEntrada")
                        .get()
                        .await()

                    val tiposEntrada = tiposSnapshot.documents.map { t ->
                        TipoEntrada(
                            id          = t.id,
                            nombre      = t.getString("nombre") ?: "",
                            descripcion = t.getString("descripcion") ?: "",
                            precio      = t.getDouble("precio") ?: 0.0
                        )
                    }

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
                            esPublica   = doc.getBoolean("esPublica") ?: true,
                            museoNombre = doc.getString("museoNombre") ?: "",
                            museoId     = museo.id,
                            tiposEntrada = tiposEntrada
                        )
                    )
                }
            }
            FirebaseResult.Success(exposiciones)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar exposiciones")
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
            doc.getString(campo) ?: ""
        }
    }
}