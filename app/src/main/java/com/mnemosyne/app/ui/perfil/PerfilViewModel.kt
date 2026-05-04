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
            _perfil.value = repository.obtenerPerfil()
        }
    }

    fun actualizarNombre(nuevoNombre: String) {
        if (nuevoNombre.isBlank()) {
            _actualizacion.value = FirebaseResult.Error("El nombre no puede estar vacío")
            return
        }
        viewModelScope.launch {
            _actualizacion.value = repository.actualizarNombre(nuevoNombre)
            if (_actualizacion.value is FirebaseResult.Success) cargarPerfil()
        }
    }

    fun guardarCambios(nuevoNombre: String, fotoUri: android.net.Uri?) {
        viewModelScope.launch {
            if (fotoUri != null) {
                val resultado = repository.subirFotoPerfil(fotoUri)
                _actualizacion.value = when (resultado) {
                    is FirebaseResult.Success -> FirebaseResult.Success(Unit)
                    is FirebaseResult.Error   -> FirebaseResult.Error(resultado.mensaje)
                    is FirebaseResult.Loading -> FirebaseResult.Loading
                }
            }
            val nombreActual = (perfil.value as? FirebaseResult.Success)?.data?.nombre
            if (nuevoNombre.isNotBlank() && nuevoNombre != nombreActual) {
                _actualizacion.value = repository.actualizarNombre(nuevoNombre)
            }
            cargarPerfil()
        }
    }
}