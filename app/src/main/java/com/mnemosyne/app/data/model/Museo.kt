package com.mnemosyne.app.data.model

data class Museo(
    val id: String = "",
    val nombre: String = "",
    val ciudad: String = "",
    val descripcion: String = "",
    val imagenUrl: String = "",
    val web: String = "",
    val destacado: Boolean = false
)