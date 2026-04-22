package com.mnemosyne.app.data.model

data class Obra(
    val id: String = "",
    val nombre: String = "",
    val autor: String = "",
    val siglo: String = "",
    val cultura: String = "",
    val descripcion: String = "",
    val imagenUrl: String = "",
    val disponible: Boolean = true
)
