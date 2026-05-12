package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class CuriosidadDelDiaRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerCuriosidadDelDia(): FirebaseResult<String> {
        return try {
            val doc = db.collection("curiosidades")
                .document("lista")
                .get()
                .await()

            @Suppress("UNCHECKED_CAST")
            val datos = doc.get("datos") as? List<String>
                ?: return FirebaseResult.Error("No se encontraron curiosidades")

            if (datos.isEmpty()) return FirebaseResult.Error("La lista está vacía")

            val diaDelAno = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val indice = diaDelAno % datos.size

            FirebaseResult.Success(datos[indice])
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar la curiosidad")
        }
    }
}