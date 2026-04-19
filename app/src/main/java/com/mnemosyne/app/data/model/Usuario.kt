package com.mnemosyne.app.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val fotoPerfil: String = "",
    val favoritos: List<String> = emptyList()
)