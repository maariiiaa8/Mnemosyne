package com.mnemosyne.app.ui.compra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctions
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.data.model.Pedido
import com.mnemosyne.app.data.repository.CarritoRepository
import com.mnemosyne.app.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class EstadoCompra {
    object Idle : EstadoCompra()
    object Cargando : EstadoCompra()
    data class Error(val mensaje: String) : EstadoCompra()
}

class CompraViewModel : ViewModel() {

    private val functions = FirebaseFunctions.getInstance()
    private val pedidoRepository = PedidoRepository()
    private val carritoRepository = CarritoRepository()

    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> = _clientSecret

    private val _estado = MutableStateFlow<EstadoCompra>(EstadoCompra.Idle)
    val estado: StateFlow<EstadoCompra> = _estado

    private var itemsCarrito: List<ItemCarrito> = emptyList()

    fun iniciarPago(precio: Double, items: List<ItemCarrito> = emptyList()) {
        itemsCarrito = items
        viewModelScope.launch {
            _estado.value = EstadoCompra.Cargando
            try {
                val resultado = functions
                    .getHttpsCallable("crearPaymentIntent")
                    .call(mapOf("amount" to precio))
                    .await()

                @Suppress("UNCHECKED_CAST")
                val data = resultado.data as? Map<String, Any>
                val secret = data?.get("clientSecret") as? String

                if (secret != null) {
                    _clientSecret.value = secret
                    _estado.value = EstadoCompra.Idle
                } else {
                    _estado.value = EstadoCompra.Error("No se recibió el clientSecret")
                }
            } catch (e: Exception) {
                _estado.value = EstadoCompra.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun guardarPedidoTrasCompra(total: Double) {
        viewModelScope.launch {
            val pedido = Pedido(
                items = itemsCarrito,
                total = total
            )
            pedidoRepository.guardarPedido(pedido)
            carritoRepository.vaciarCarrito()
        }
    }

    fun onError(mensaje: String) {
        _estado.value = EstadoCompra.Error(mensaje)
    }
}