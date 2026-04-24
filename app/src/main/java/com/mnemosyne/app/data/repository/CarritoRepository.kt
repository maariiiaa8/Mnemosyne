package com.mnemosyne.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class CarritoRepository {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Referencia a los items del carrito del usuario actual
    private fun itemsRef() = db.collection("carritos")
        .document(auth.currentUser!!.uid)
        .collection("items")

    // Obtiene todos los items del carrito
    suspend fun obtenerItems(): FirebaseResult<List<ItemCarrito>> {
        return try {
            val snapshot = itemsRef().get().await()
            val items = snapshot.documents.map { doc ->
                ItemCarrito(
                    id                = doc.id,
                    exposicionId      = doc.getString("exposicionId") ?: "",
                    exposicionTitulo  = doc.getString("exposicionTitulo") ?: "",
                    museoNombre       = doc.getString("museoNombre") ?: "",
                    tipoEntradaNombre = doc.getString("tipoEntradaNombre") ?: "",
                    precio            = doc.getDouble("precio") ?: 0.0,
                    cantidad          = doc.getLong("cantidad")?.toInt() ?: 1
                )
            }
            FirebaseResult.Success(items)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al cargar el carrito")
        }
    }

    // Añade un item al carrito
    suspend fun añadirItem(item: ItemCarrito): FirebaseResult<Unit> {
        return try {
            val data = mapOf(
                "exposicionId"      to item.exposicionId,
                "exposicionTitulo"  to item.exposicionTitulo,
                "museoNombre"       to item.museoNombre,
                "tipoEntradaNombre" to item.tipoEntradaNombre,
                "precio"            to item.precio,
                "cantidad"          to item.cantidad
            )
            itemsRef().add(data).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al añadir al carrito")
        }
    }

    // Elimina un item del carrito
    suspend fun eliminarItem(itemId: String): FirebaseResult<Unit> {
        return try {
            itemsRef().document(itemId).delete().await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al eliminar del carrito")
        }
    }

    // Actualiza la cantidad de un item
    suspend fun actualizarCantidad(itemId: String, cantidad: Int): FirebaseResult<Unit> {
        return try {
            itemsRef().document(itemId).update("cantidad", cantidad).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al actualizar cantidad")
        }
    }

    // Vacía el carrito completo
    suspend fun vaciarCarrito(): FirebaseResult<Unit> {
        return try {
            val items = itemsRef().get().await()
            items.documents.forEach { it.reference.delete().await() }
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al vaciar el carrito")
        }
    }
}