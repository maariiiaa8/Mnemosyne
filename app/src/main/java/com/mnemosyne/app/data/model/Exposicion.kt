package com.mnemosyne.app.data.model

data class Exposicion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val imagenUrl: String = "",
    val destacada: Boolean = false,
    val museoNombre: String = ""
)