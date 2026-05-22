package com.acacioswork.model

data class Proveedor(
    val id: Long? = null,
    val nombre: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val cuentaBancaria: String? = null,
    val idTipoDocumento: Long,
    val numeroDocumento: String,
    val activo: Int = 1,
    val email: String? = null
)
