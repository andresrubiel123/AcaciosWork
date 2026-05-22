package com.acacioswork.model

data class Usuario(
    val id: Long? = null,
    val idTipoDocumento: Long? = null,
    val numeroDocumento: String,
    val nombre: String,
    val apellido: String,
    val telefono: String? = null,
    val email: String,
    val usuario: String,
    val clave: String? = null,
    val activo: Int = 1,
    val idRol: Long
)
