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

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = FirebaseResult.Loading
            _productos.value = repository.obtenerProductos()
        }
    }
}