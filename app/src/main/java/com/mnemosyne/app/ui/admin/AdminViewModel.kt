package com.mnemosyne.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.data.repository.AdminRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _museos = MutableLiveData<Map<String, String>>()
    val museos: LiveData<Map<String, String>> = _museos

    private val _exposiciones = MutableLiveData<FirebaseResult<List<Exposicion>>>()
    val exposiciones: LiveData<FirebaseResult<List<Exposicion>>> = _exposiciones

    private val _noticias = MutableLiveData<FirebaseResult<List<Noticia>>>()
    val noticias: LiveData<FirebaseResult<List<Noticia>>> = _noticias

    private val _obras = MutableLiveData<FirebaseResult<List<Obra>>>()
    val obras: LiveData<FirebaseResult<List<Obra>>> = _obras

    private val _stock = MutableLiveData<FirebaseResult<List<Stock>>>()
    val stock: LiveData<FirebaseResult<List<Stock>>> = _stock

    private val _operacion = MutableLiveData<FirebaseResult<Unit>>()
    val operacion: LiveData<FirebaseResult<Unit>> = _operacion

    var museoSeleccionado: String = ""
        private set

    init {
        cargarMuseos()
    }

    fun cargarMuseos() {
        viewModelScope.launch {
            val result = repository.obtenerMuseos()
            if (result is FirebaseResult.Success) {
                _museos.postValue(result.data)
                if (result.data.isNotEmpty() && museoSeleccionado.isEmpty()) {
                    seleccionarMuseo(result.data.keys.first())
                }
            }
        }
    }

    fun seleccionarMuseo(museoId: String) {
        museoSeleccionado = museoId
        cargarExposiciones()
        cargarNoticias()
        cargarObras()
        cargarStock()
    }

    // ── EXPOSICIONES ──────────────────────────────────────
    fun cargarExposiciones() {
        viewModelScope.launch {
            _exposiciones.postValue(FirebaseResult.Loading)
            _exposiciones.postValue(repository.obtenerExposiciones(museoSeleccionado))
        }
    }

    fun crearExposicion(exposicion: Exposicion) {
        viewModelScope.launch {
            val result = repository.crearExposicion(museoSeleccionado, exposicion)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarExposiciones()
        }
    }

    fun actualizarExposicion(exposicion: Exposicion) {
        viewModelScope.launch {
            val result = repository.actualizarExposicion(museoSeleccionado, exposicion)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarExposiciones()
        }
    }

    fun eliminarExposicion(exposicionId: String) {
        viewModelScope.launch {
            val result = repository.eliminarExposicion(museoSeleccionado, exposicionId)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarExposiciones()
        }
    }

    // ── NOTICIAS ──────────────────────────────────────────
    fun cargarNoticias() {
        viewModelScope.launch {
            _noticias.postValue(FirebaseResult.Loading)
            _noticias.postValue(repository.obtenerNoticias(museoSeleccionado))
        }
    }

    fun crearNoticia(noticia: Noticia) {
        viewModelScope.launch {
            val result = repository.crearNoticia(museoSeleccionado, noticia)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarNoticias()
        }
    }

    fun actualizarNoticia(noticia: Noticia) {
        viewModelScope.launch {
            val result = repository.actualizarNoticia(museoSeleccionado, noticia)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarNoticias()
        }
    }

    fun eliminarNoticia(noticiaId: String) {
        viewModelScope.launch {
            val result = repository.eliminarNoticia(museoSeleccionado, noticiaId)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarNoticias()
        }
    }

    // ── OBRAS ─────────────────────────────────────────────
    fun cargarObras() {
        viewModelScope.launch {
            _obras.postValue(FirebaseResult.Loading)
            _obras.postValue(repository.obtenerObras(museoSeleccionado))
        }
    }

    fun crearObra(obra: Obra) {
        viewModelScope.launch {
            val result = repository.crearObra(museoSeleccionado, obra)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarObras()
        }
    }

    fun actualizarObra(obra: Obra) {
        viewModelScope.launch {
            val result = repository.actualizarObra(museoSeleccionado, obra)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarObras()
        }
    }

    fun eliminarObra(obraId: String) {
        viewModelScope.launch {
            val result = repository.eliminarObra(museoSeleccionado, obraId)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarObras()
        }
    }

    // ── STOCK ─────────────────────────────────────────────
    fun cargarStock() {
        viewModelScope.launch {
            _stock.postValue(FirebaseResult.Loading)
            _stock.postValue(repository.obtenerStock(museoSeleccionado))
        }
    }

    fun crearProducto(producto: Stock) {
        viewModelScope.launch {
            val result = repository.crearProducto(museoSeleccionado, producto)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarStock()
        }
    }

    fun actualizarProducto(producto: Stock) {
        viewModelScope.launch {
            val result = repository.actualizarProducto(museoSeleccionado, producto)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarStock()
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            val result = repository.eliminarProducto(museoSeleccionado, productoId)
            _operacion.postValue(result)
            if (result is FirebaseResult.Success) cargarStock()
        }
    }
}