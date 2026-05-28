package com.acacioswork.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Categoría para la aplicación Android.
 * @author RADJ
 */
data class Categoria(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String
)
