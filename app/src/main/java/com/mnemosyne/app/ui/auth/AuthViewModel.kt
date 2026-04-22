package com.mnemosyne.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mnemosyne.app.data.repository.AuthRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // Estado del login
    private val _loginState = MutableLiveData<FirebaseResult<FirebaseUser>>()
    val loginState: LiveData<FirebaseResult<FirebaseUser>> = _loginState

    // Estado del registro
    private val _registroState = MutableLiveData<FirebaseResult<FirebaseUser>>()
    val registroState: LiveData<FirebaseResult<FirebaseUser>> = _registroState

    fun login(email: String, password: String) {
        if (!validarCampos(email, password)) {
            _loginState.value = FirebaseResult.Error("Rellena todos los campos")
            return
        }
        _loginState.value = FirebaseResult.Loading
        viewModelScope.launch {
            _loginState.value = repository.login(email, password)
        }
    }

    fun registro(nombre: String, email: String, password: String) {
        if (nombre.isBlank() || !validarCampos(email, password)) {
            _registroState.value = FirebaseResult.Error("Rellena todos los campos")
            return
        }
        _registroState.value = FirebaseResult.Loading
        viewModelScope.launch {
            _registroState.value = repository.registro(nombre, email, password)
        }
    }

    fun hayUsuarioLogueado(): Boolean {
        return repository.usuarioActual() != null
    }

    private fun validarCampos(email: String, password: String): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }
}