package com.acacioswork.model

data class Producto(
    val id: Long? = null,
    val nombre: String,
    val codigoBarras: String?,
    val cantidad: Int,
    val precioVenta: Double,
    val stockMinimo: Int
)
