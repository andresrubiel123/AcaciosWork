package com.acacioswork.model

data class DetalleVenta(
    val id: Long? = null,
    val idVenta: Long? = null,
    val idProducto: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double = cantidad * precioUnitario
)
