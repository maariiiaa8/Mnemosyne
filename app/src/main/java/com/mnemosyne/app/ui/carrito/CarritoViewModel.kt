package com.mnemosyne.app.ui.carrito

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.data.repository.CarritoRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val repository = CarritoRepository()

    private val _items = MutableLiveData<FirebaseResult<List<ItemCarrito>>>()
    val items: LiveData<FirebaseResult<List<ItemCarrito>>> = _items

    private val _operacion = MutableLiveData<FirebaseResult<Unit>>()
    val operacion: LiveData<FirebaseResult<Unit>> = _operacion

    init {
        cargarCarrito()
    }

    fun cargarCarrito() {
        viewModelScope.launch {
            _items.value = FirebaseResult.Loading
            _items.value = repository.obtenerItems()
        }
    }

    fun añadirItem(item: ItemCarrito) {
        viewModelScope.launch {
            _operacion.value = FirebaseResult.Loading
            val result = repository.añadirItem(item)
            _operacion.value = result
            if (result is FirebaseResult.Success) cargarCarrito()
        }
    }

    fun eliminarItem(itemId: String) {
        viewModelScope.launch {
            repository.eliminarItem(itemId)
            cargarCarrito()
        }
    }

    fun actualizarCantidad(itemId: String, cantidad: Int) {
        viewModelScope.launch {
            if (cantidad <= 0) {
                repository.eliminarItem(itemId)
            } else {
                repository.actualizarCantidad(itemId, cantidad)
            }
            cargarCarrito()
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciarCarrito()
            cargarCarrito()
        }
    }

    // Calcula el total del carrito
    fun calcularTotal(items: List<ItemCarrito>): Double {
        return items.sumOf { it.precio * it.cantidad }
    }
}