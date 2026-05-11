package com.mnemosyne.app.ui.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnemosyne.app.data.model.Usuario
import com.mnemosyne.app.data.repository.UsuarioRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private val _perfil = MutableLiveData<FirebaseResult<Usuario>>()
    val perfil: LiveData<FirebaseResult<Usuario>> = _perfil

    private val _actualizacion = MutableLiveData<FirebaseResult<Unit>>()
    val actualizacion: LiveData<FirebaseResult<Unit>> = _actualizacion

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        _perfil.value = FirebaseResult.Loading
        viewModelScope.launch {
            _perfil.postValue(repository.obtenerPerfil())
        }
    }

    fun guardarCambios(nuevoNombre: String) {
        viewModelScope.launch {
            val nombreActual = (perfil.value as? FirebaseResult.Success)?.data?.nombre

            if (nuevoNombre.isBlank()) {
                _actualizacion.postValue(FirebaseResult.Error("El nombre no puede estar vacío"))
                return@launch
            }

            if (nuevoNombre == nombreActual) {
                _actualizacion.postValue(FirebaseResult.Success(Unit))
                return@launch
            }

            val resultado = repository.actualizarNombre(nuevoNombre)
            if (resultado is FirebaseResult.Error) {
                _actualizacion.postValue(FirebaseResult.Error(resultado.mensaje))
                return@launch
            }

            cargarPerfil()
            _actualizacion.postValue(FirebaseResult.Success(Unit))
        }
    }
}