package com.mnemosyne.app.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Pedido
import com.mnemosyne.app.data.repository.PedidoRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MisEntradasViewModel : ViewModel() {

    private val repository = PedidoRepository()

    private val _pedidos = MutableStateFlow<FirebaseResult<List<Pedido>>>(FirebaseResult.Loading)
    val pedidos: StateFlow<FirebaseResult<List<Pedido>>> = _pedidos

    init {
        cargarPedidos()
    }

    fun cargarPedidos() {
        viewModelScope.launch {
            _pedidos.value = FirebaseResult.Loading
            _pedidos.value = repository.obtenerPedidos()
        }
    }
}