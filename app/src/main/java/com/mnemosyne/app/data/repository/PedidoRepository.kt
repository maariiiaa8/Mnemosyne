package com.mnemosyne.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mnemosyne.app.data.model.Pedido
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.tasks.await

class PedidoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarPedido(pedido: Pedido): FirebaseResult<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return FirebaseResult.Error("No autenticado")
            val ref = db.collection("usuarios").document(uid)
                .collection("pedidos").document()
            ref.set(pedido.copy(id = ref.id, uid = uid)).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al guardar pedido")
        }
    }

    suspend fun obtenerPedidos(): FirebaseResult<List<Pedido>> {
        return try {
            val uid = auth.currentUser?.uid ?: return FirebaseResult.Error("No autenticado")
            val snapshot = db.collection("usuarios").document(uid)
                .collection("pedidos")
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val pedidos = snapshot.documents.mapNotNull { it.toObject(Pedido::class.java) }
            FirebaseResult.Success(pedidos)
        } catch (e: Exception) {
            FirebaseResult.Error(e.message ?: "Error al obtener pedidos")
        }
    }
}