package com.mnemosyne.app.data.model

data class Stock(
    val id: String = "",
    val nombreProducto: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val imagenUrl: String = ""
)
