package com.mnemosyne.app.ui.museos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.data.repository.MuseoRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class MuseosViewModel : ViewModel() {

    private val repository = MuseoRepository()

    private val _museos = MutableLiveData<FirebaseResult<List<Museo>>>()
    val museos: LiveData<FirebaseResult<List<Museo>>> = _museos

    init {
        cargarMuseos()
    }

    fun cargarMuseos() {
        viewModelScope.launch {
            _museos.value = FirebaseResult.Loading
            _museos.value = repository.obtenerMuseos()
        }
    }
}