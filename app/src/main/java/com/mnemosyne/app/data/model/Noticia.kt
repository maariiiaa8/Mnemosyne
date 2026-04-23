package com.mnemosyne.app.data.model

data class Noticia(
    val id: String = "",
    val titulo: String = "",
    val contenido: String = "",
    val fecha: String = "",
    val imagenUrl: String = "",
    val destacada: Boolean = false,
    val museoNombre: String = ""
)