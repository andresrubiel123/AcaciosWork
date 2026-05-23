package com.acacioswork.model

data class Producto(
    val id: Long? = null,
    val codigoBarras: String? = null,
    val nombre: String,
    val stockActual: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val iva: Double,
    val idCategoria: Long? = null,
    val idProveedor: Long? = null,
    val estado: Int = 1,
    val stockMinimo: Int,
    val stockOptimo: Int = 200,
    val unidadMedida: String = "Unidad"
)
