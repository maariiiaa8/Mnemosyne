package com.mnemosyne.app.ui.museos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.data.repository.ExposicionRepository
import com.mnemosyne.app.data.repository.ObraRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class DetalleMuseoViewModel : ViewModel() {

    private val obraRepository = ObraRepository()
    private val exposicionRepository = ExposicionRepository()

    private val _obras = MutableLiveData<FirebaseResult<List<Obra>>>()
    val obras: LiveData<FirebaseResult<List<Obra>>> = _obras

    private val _exposiciones = MutableLiveData<FirebaseResult<List<Exposicion>>>()
    val exposiciones: LiveData<FirebaseResult<List<Exposicion>>> = _exposiciones

    fun cargarDatos(museoId: String) {
        viewModelScope.launch {
            _obras.value = FirebaseResult.Loading
            _obras.value = obraRepository.obtenerObrasDeMuseo(museoId)
        }
        viewModelScope.launch {
            _exposiciones.value = FirebaseResult.Loading
            _exposiciones.value = exposicionRepository.obtenerExposicionesDeMuseo(museoId)
        }
    }
}