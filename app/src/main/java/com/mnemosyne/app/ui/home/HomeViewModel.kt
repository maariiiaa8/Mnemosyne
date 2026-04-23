package com.mnemosyne.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.data.repository.HomeRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = HomeRepository()

    private val _exposiciones = MutableLiveData<FirebaseResult<List<Exposicion>>>()
    val exposiciones: LiveData<FirebaseResult<List<Exposicion>>> = _exposiciones

    private val _noticias = MutableLiveData<FirebaseResult<List<Noticia>>>()
    val noticias: LiveData<FirebaseResult<List<Noticia>>> = _noticias

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _exposiciones.value = FirebaseResult.Loading
            _noticias.value = FirebaseResult.Loading
            _exposiciones.value = repository.obtenerExposicionesDestacadas()
            _noticias.value = repository.obtenerNoticiasDestacadas()
        }
    }
}