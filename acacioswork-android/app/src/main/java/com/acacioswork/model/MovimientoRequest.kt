package com.acacioswork.model

/**
 * Representa la solicitud para registrar un movimiento de inventario.
 * @author RADJ
 */
data class MovimientoRequest(
    val idProducto: Long,
    val tipoMovimiento: String,
    val cantidad: Int,
    val referencia: String? = null,
    val observacion: String? = null,
    val idUsuario: Long
)
