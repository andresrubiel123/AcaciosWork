package com.acacioswork.network

import com.acacioswork.model.*
import retrofit2.http.*

interface ApiService {

    // Autenticación
    @POST("usuarios/login")
    suspend fun login(@Body req: LoginRequest): ApiResponse<LoginResponse>

    // Productos
    @GET("productos")
    suspend fun getProductos(): ApiResponse<List<Producto>>

    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): ApiResponse<Producto>

    @POST("productos")
    suspend fun createProducto(@Body producto: Producto): ApiResponse<Producto>

    @PUT("productos/{id}")
    suspend fun updateProducto(@Path("id") id: Long, @Body producto: Producto): ApiResponse<Producto>

    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Long): ApiResponse<Void>

    // Clientes
    @GET("clientes")
    suspend fun getClientes(): ApiResponse<List<Cliente>>

    @GET("clientes/{id}")
    suspend fun getClienteById(@Path("id") id: Long): ApiResponse<Cliente>

    @POST("clientes")
    suspend fun createCliente(@Body cliente: Cliente): ApiResponse<Cliente>

    @PUT("clientes/{id}")
    suspend fun updateCliente(@Path("id") id: Long, @Body cliente: Cliente): ApiResponse<Cliente>

    @DELETE("clientes/{id}")
    suspend fun deleteCliente(@Path("id") id: Long): ApiResponse<Void>

    // Proveedores
    @GET("proveedores")
    suspend fun getProveedores(): ApiResponse<List<Proveedor>>

    @GET("proveedores/{id}")
    suspend fun getProveedorById(@Path("id") id: Long): ApiResponse<Proveedor>

    @POST("proveedores")
    suspend fun createProveedor(@Body proveedor: Proveedor): ApiResponse<Proveedor>

    @PUT("proveedores/{id}")
    suspend fun updateProveedor(@Path("id") id: Long, @Body proveedor: Proveedor): ApiResponse<Proveedor>

    @DELETE("proveedores/{id}")
    suspend fun deleteProveedor(@Path("id") id: Long): ApiResponse<Void>

    // Usuarios
    @GET("usuarios")
    suspend fun getUsuarios(): ApiResponse<List<Usuario>>

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): ApiResponse<Usuario>

    @PUT("usuarios/{numeroDocumento}")
    suspend fun updateUsuario(@Path("numeroDocumento") numeroDocumento: String, @Body usuario: Usuario): ApiResponse<Usuario>

    @DELETE("usuarios/{numeroDocumento}")
    suspend fun deleteUsuario(@Path("numeroDocumento") numeroDocumento: String): ApiResponse<String>

    // Ventas
    @POST("ventas")
    suspend fun createVenta(@Body venta: Venta): ApiResponse<Venta>

    @GET("ventas")
    suspend fun getVentas(): ApiResponse<List<Venta>>

    // Configuración
    @GET("configuracion")
    suspend fun getConfiguracion(): ApiResponse<Configuracion>

    @PUT("configuracion")
    suspend fun updateConfiguracion(@Body config: Configuracion): ApiResponse<Configuracion>
}
