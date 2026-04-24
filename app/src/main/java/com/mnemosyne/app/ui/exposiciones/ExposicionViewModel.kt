package com.mnemosyne.app.ui.exposiciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.repository.ExposicionRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class ExposicionViewModel : ViewModel() {

    private val repository = ExposicionRepository()

    private val _exposiciones = MutableLiveData<FirebaseResult<List<Exposicion>>>()
    val exposiciones: LiveData<FirebaseResult<List<Exposicion>>> = _exposiciones

    // Lista de museos disponibles para el filtro
    private val _museos = MutableLiveData<List<String>>()
    val museos: LiveData<List<String>> = _museos

    // Museo seleccionado para filtrar (null = todos)
    private val _museoFiltro = MutableLiveData<String?>(null)
    val museoFiltro: LiveData<String?> = _museoFiltro

    init {
        cargarExposiciones()
    }

    fun cargarExposiciones() {
        viewModelScope.launch {
            _exposiciones.value = FirebaseResult.Loading
            val result = repository.obtenerTodasExposiciones()
            _exposiciones.value = result

            // Extraemos la lista de museos únicos para el filtro
            if (result is FirebaseResult.Success) {
                _museos.value = result.data.map { it.museoNombre }.distinct()
            }
        }
    }

    fun filtrarPorMuseo(museo: String?) {
        _museoFiltro.value = museo
    }

    // Devuelve las exposiciones filtradas según el museo seleccionado
    fun exposicionesFiltradas(): List<Exposicion> {
        val todas = (_exposiciones.value as? FirebaseResult.Success)?.data ?: return emptyList()
        val filtro = _museoFiltro.value
        return if (filtro == null) todas else todas.filter { it.museoNombre == filtro }
    }
}