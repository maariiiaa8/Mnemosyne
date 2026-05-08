package com.mnemosyne.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class StockRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("stock")

    suspend fun obtenerProductos(): FirebaseResult<List<Stock>> {
        return try {
            val snapshot = coleccion.get().await()
            val lista = snapshot.documents.map { doc ->
                doc.toObject(Stock::class.java)!!.copy(id = doc.id)
            }
            FirebaseResult.Success(lista)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar productos")
        }
    }

    suspend fun decrementarStock(productoId: String, cantidad: Int): FirebaseResult<Unit> {
        return try {
            val doc = coleccion.document(productoId).get().await()
            val stockActual = doc.getLong("cantidad")?.toInt() ?: 0
            val nuevoStock = (stockActual - cantidad).coerceAtLeast(0)
            coleccion.document(productoId).update("cantidad", nuevoStock).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar stock")
        }
    }
}