package com.mnemosyne.app.data.model

data class Reserva(
    val id: String = "",
    val usuarioId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val numPersonas: Int = 1,
    val estado: String = "pendiente"
)