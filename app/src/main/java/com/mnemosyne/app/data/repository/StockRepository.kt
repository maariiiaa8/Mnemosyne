package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class StockRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerMuseos(): FirebaseResult<Map<String, String>> {
        return try {
            val snapshot = db.collection("museos").get().await()
            val museos = snapshot.documents.associate { doc ->
                doc.id to (doc.getString("nombre") ?: doc.id)
            }
            FirebaseResult.Success(museos)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar museos")
        }
    }

    suspend fun obtenerProductosPorMuseo(museoId: String): FirebaseResult<List<Stock>> {
        return try {
            val snapshot = db.collection("museos")
                .document(museoId)
                .collection("stock")
                .get()
                .await()
            val lista = snapshot.documents.map { doc ->
                doc.toObject(Stock::class.java)!!.copy(
                    id = doc.id,
                    museoId = museoId
                )
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar productos")
        }
    }

    suspend fun obtenerTodosLosProductos(): FirebaseResult<List<Stock>> {
        return try {
            val museosSnapshot = db.collection("museos").get().await()
            val todos = mutableListOf<Stock>()
            for (museoDoc in museosSnapshot.documents) {
                val stockSnapshot = museoDoc.reference.collection("stock").get().await()
                val productos = stockSnapshot.documents.map { doc ->
                    doc.toObject(Stock::class.java)!!.copy(
                        id = doc.id,
                        museoId = museoDoc.id
                    )
                }
                todos.addAll(productos)
            }
            FirebaseResult.Success(todos)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar productos")
        }
    }

    suspend fun decrementarStock(museoId: String, productoId: String, cantidad: Int): FirebaseResult<Unit> {
        return try {
            val ref = db.collection("museos")
                .document(museoId)
                .collection("stock")
                .document(productoId)
            val doc = ref.get().await()
            val stockActual = doc.getLong("cantidad")?.toInt() ?: 0
            val nuevoStock = (stockActual - cantidad).coerceAtLeast(0)
            ref.update("cantidad", nuevoStock).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar stock")
        }
    }
}