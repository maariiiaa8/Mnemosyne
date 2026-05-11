package com.mnemosyne.app.ui.tienda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.data.repository.StockRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {

    private val repository = StockRepository()

    private val _productos = MutableLiveData<FirebaseResult<List<Stock>>>()
    val productos: LiveData<FirebaseResult<List<Stock>>> = _productos

    // museosId -> nombre legible
    private val _museos = MutableLiveData<Map<String, String>>()
    val museos: LiveData<Map<String, String>> = _museos

    private var museoSeleccionado: String? = null

    init {
        cargarMuseos()
        cargarProductos()
    }

    fun cargarMuseos() {
        viewModelScope.launch {
            val resultado = repository.obtenerMuseos()
            if (resultado is FirebaseResult.Success) {
                _museos.value = resultado.data
            }
        }
    }

    fun cargarProductos(museoId: String? = null) {
        museoSeleccionado = museoId
        viewModelScope.launch {
            _productos.value = FirebaseResult.Loading
            _productos.value = if (museoId != null) {
                repository.obtenerProductosPorMuseo(museoId)
            } else {
                repository.obtenerTodosLosProductos()
            }
        }
    }
}