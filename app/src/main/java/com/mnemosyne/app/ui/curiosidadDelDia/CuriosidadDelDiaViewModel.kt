package com.mnemosyne.app.ui.curiosidadDelDia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.repository.CuriosidadDelDiaRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class CuriosidadDelDiaViewModel : ViewModel() {

    private val repository = CuriosidadDelDiaRepository()

    private val _curiosidad = MutableLiveData<FirebaseResult<String>>()
    val curiosidad: LiveData<FirebaseResult<String>> = _curiosidad

    init {
        cargarCuriosidad()
    }

    fun cargarCuriosidad() {
        viewModelScope.launch {
            _curiosidad.value = FirebaseResult.Loading
            _curiosidad.value = repository.obtenerCuriosidadDelDia()
        }
    }
}