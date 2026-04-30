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
                    .collection("exposiciones")
                    .get()
                    .await()

                for (doc in expos.documents) {
                    // Leemos tiposEntrada como array embebido en el documento
                    @Suppress("UNCHECKED_CAST")
                    val tiposRaw = doc.get("tiposEntrada") as? List<Map<String, Any>> ?: emptyList()

                    val tiposEntrada = tiposRaw.map { t ->
                        TipoEntrada(
                            id          = t["id"] as? String ?: "",
                            nombre      = t["nombre"] as? String ?: "",
                            descripcion = t["descripcion"] as? String ?: "",
                            precio      = (t["precio"] as? Number)?.toDouble() ?: 0.0
                        )
                    }

                    exposiciones.add(
                        Exposicion(
                            id           = doc.id,
                            titulo       = doc.getString("titulo") ?: "",
                            descripcion  = doc.getString("descripcion") ?: "",
                            fechaInicio  = parsearFecha(doc, "fechaInicio"),
                            fechaFin     = parsearFecha(doc, "fechaFin"),
                            imagenUrl    = doc.getString("imagenUrl") ?: "",
                            destacada    = doc.getBoolean("destacada") ?: false,
                            esPublica    = doc.getBoolean("esPublica") ?: true,
                            museoNombre  = doc.getString("museoNombre") ?: "",
                            museoId      = museo.id,
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