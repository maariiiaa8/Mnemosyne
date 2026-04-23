package com.mnemosyne.app.utils

sealed class FirebaseResult<out T> {
    data class Success<out T>(val data: T) : FirebaseResult<T>()
    data class Error(val mensaje: String) : FirebaseResult<Nothing>()
    object Loading : FirebaseResult<Nothing>()
}