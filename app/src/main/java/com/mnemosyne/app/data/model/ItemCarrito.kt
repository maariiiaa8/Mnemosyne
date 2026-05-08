package com.mnemosyne.app.data.model

data class ItemCarrito(
    val id: String = "",
    val exposicionId: String = "",
    val exposicionTitulo: String = "",
    val museoNombre: String = "",
    val tipoEntradaNombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val categoria: String = "entrada"
)