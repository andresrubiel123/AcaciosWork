package com.acacioswork.model

data class Venta(
    val id: Long? = null,
    val fechaHora: String? = null,
    val idCliente: Long? = null,
    val idUsuario: Long? = null,
    val valorTotal: Double = 0.0,
    val detalles: List<DetalleVenta> = emptyList()
)
