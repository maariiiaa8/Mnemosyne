package com.mnemosyne.app.ui.noticias

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.data.repository.NoticiaRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class NoticiasViewModel : ViewModel() {

    private val repository = NoticiaRepository()

    private val _noticias = MutableLiveData<FirebaseResult<List<Noticia>>>()
    val noticias: LiveData<FirebaseResult<List<Noticia>>> = _noticias

    private val _museos = MutableLiveData<List<String>>()
    val museos: LiveData<List<String>> = _museos

    private val _museoFiltro = MutableLiveData<String?>(null)
    val museoFiltro: LiveData<String?> = _museoFiltro

    init {
        cargarNoticias()
    }

    fun cargarNoticias() {
        viewModelScope.launch {
            _noticias.value = FirebaseResult.Loading
            val result = repository.obtenerNoticias()
            _noticias.value = result

            if (result is FirebaseResult.Success) {
                _museos.value = result.data.map { it.museoNombre }.distinct()
            }
        }
    }

    fun filtrarPorMuseo(museo: String?) {
        _museoFiltro.value = museo
    }

    fun noticiasFiltradas(): List<Noticia> {
        val todas = (_noticias.value as? FirebaseResult.Success)?.data ?: return emptyList()
        val filtro = _museoFiltro.value
        return if (filtro == null) todas else todas.filter { it.museoNombre == filtro }
    }
}