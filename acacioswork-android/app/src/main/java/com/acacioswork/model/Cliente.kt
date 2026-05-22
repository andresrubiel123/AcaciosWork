package com.acacioswork.model

data class Cliente(
    val id: Long? = null,
    val idTipoDocumento: Long? = null,
    val numeroDocumento: String,
    val nombre: String,
    val telefono: String? = null,
    val email: String? = null,
    val direccion: String? = null,
    val frecuente: Boolean = false,
    val activo: Int = 1
)
