package com.acacioswork.network

import com.acacioswork.model.Producto
import com.acacioswork.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/api/usuarios/login")
    fun login(@Body user: Usuario): Call<String>

    @GET("/api/productos")
    fun getProductos(): Call<List<Producto>>
}
