package com.mnemosyne.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mnemosyne.app.data.repository.AuthRepository
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch
import com.mnemosyne.app.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val _usuarioDatos = MutableLiveData<FirebaseResult<Usuario>>()
    val usuarioDatos: LiveData<FirebaseResult<Usuario>> = _usuarioDatos
    // Estado del login
    private val _loginState = MutableLiveData<FirebaseResult<FirebaseUser>>()
    val loginState: LiveData<FirebaseResult<FirebaseUser>> = _loginState

    // Estado del registro
    private val _registroState = MutableLiveData<FirebaseResult<FirebaseUser>>()
    val registroState: LiveData<FirebaseResult<FirebaseUser>> = _registroState

    init {
        obtenerDatosUsuarioActual()
    }
    fun obtenerDatosUsuarioActual() {
        val firebaseUser = repository.usuarioActual()
        if (firebaseUser != null) {
            _usuarioDatos.value = FirebaseResult.Loading
            viewModelScope.launch {
                // El repositorio debe tener una función que consulte Firestore
                // Ejemplo: db.collection("usuarios").document(uid).get()
                _usuarioDatos.value = repository.obtenerDatosUsuario(firebaseUser.uid)
            }
        } else {
            _usuarioDatos.value = FirebaseResult.Error("No hay sesión activa")
        }
    }
    fun login(email: String, password: String) {
        // Validamos los campos localmente antes de hacer ninguna llamada a Firebase
        // para evitar peticiones innecesarias y dar feedback inmediato al usuario
        when {
            email.isBlank() && password.isBlank() -> {
                _loginState.value = FirebaseResult.Error("Rellena el correo y la contraseña")
                return
            }
            email.isBlank() -> {
                _loginState.value = FirebaseResult.Error("Introduce tu correo electrónico")
                return
            }
            password.isBlank() -> {
                _loginState.value = FirebaseResult.Error("Introduce tu contraseña")
                return
            }
        }
        // Actualizamos el estado a Loading para mostrar el spinner en la UI
        // mientras esperamos la respuesta de Firebase Authentication
        _loginState.value = FirebaseResult.Loading
        viewModelScope.launch {
            // Llamamos al repositorio que gestiona la autenticación con Firebase
            // y actualizamos el estado con el resultado (Success o Error)
            _loginState.value = repository.login(email, password)
        }
    }

    fun registro(nombre: String, email: String, password: String) {
        if (nombre.isBlank() || !validarCampos(email, password)) {
            _registroState.value = FirebaseResult.Error("Rellene todos los campos")
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

    fun cerrarSesion() {
        repository.cerrarSesion()
        _loginState.value = null
    }

    private fun validarCampos(email: String, password: String): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }
}