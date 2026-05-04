package com.mnemosyne.app.data.model

data class Pedido(
    val id: String = "",
    val uid: String = "",
    val items: List<ItemCarrito> = emptyList(),
    val total: Double = 0.0,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "completado"
)