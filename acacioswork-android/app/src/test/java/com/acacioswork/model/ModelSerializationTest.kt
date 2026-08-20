package com.acacioswork.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ModelSerializationTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun testProductoSerialization() {
        val json = """
            {
                "id": 1,
                "nombre": "Arroz 1Kg",
                "codigoBarras": "7701234567890",
                "precioCompra": 3200.0,
                "precioVenta": 4500.0,
                "stockActual": 50,
                "stockMinimo": 10,
                "iva": 19.0,
                "fechaVencimiento": "2026-12-31"
            }
        """.trimIndent()

        val producto = gson.fromJson(json, Producto::class.java)

        assertNotNull(producto)
        assertEquals(1L, producto.id)
        assertEquals("Arroz 1Kg", producto.nombre)
        assertEquals("7701234567890", producto.codigoBarras)
        assertEquals(3200.0, producto.precioCompra, 0.001)
        assertEquals(4500.0, producto.precioVenta, 0.001)
        assertEquals(50, producto.stockActual)
        assertEquals(10, producto.stockMinimo)
        assertEquals(19.0, producto.iva, 0.001)
        assertEquals("2026-12-31", producto.fechaVencimiento)
    }

    @Test
    fun testClienteSerialization() {
        val json = """
            {
                "id": 10,
                "nombre": "Juan Perez",
                "numeroDocumento": "1012345678",
                "telefono": "3001234567",
                "email": "juan@example.com",
                "activo": 1
            }
        """.trimIndent()

        val cliente = gson.fromJson(json, Cliente::class.java)

        assertNotNull(cliente)
        assertEquals(10L, cliente.id)
        assertEquals("Juan Perez", cliente.nombre)
        assertEquals("1012345678", cliente.numeroDocumento)
        assertEquals("3001234567", cliente.telefono)
        assertEquals("juan@example.com", cliente.email)
        assertEquals(1, cliente.activo)
    }

    @Test
    fun testProveedorSerialization() {
        val json = """
            {
                "id": 5,
                "nombre": "Distribuidora del Valle",
                "idTipoDocumento": 1,
                "numeroDocumento": "900123456-1",
                "telefono": "6015551234",
                "email": "contacto@valle.com"
            }
        """.trimIndent()

        val proveedor = gson.fromJson(json, Proveedor::class.java)

        assertNotNull(proveedor)
        assertEquals(5L, proveedor.id)
        assertEquals("Distribuidora del Valle", proveedor.nombre)
        assertEquals("900123456-1", proveedor.numeroDocumento)
        assertEquals("contacto@valle.com", proveedor.email)
    }

    @Test
    fun testUsuarioSerialization() {
        val json = """
            {
                "id": 2,
                "nombre": "Carlos",
                "apellido": "Auxiliar",
                "numeroDocumento": "123456",
                "email": "carlos@example.com",
                "usuario": "carlos",
                "idRol": 2,
                "activo": 1
            }
        """.trimIndent()

        val usuario = gson.fromJson(json, Usuario::class.java)

        assertNotNull(usuario)
        assertEquals(2L, usuario.id)
        assertEquals("Carlos", usuario.nombre)
        assertEquals("carlos", usuario.usuario)
        assertEquals(2L, usuario.idRol)
        assertEquals(1, usuario.activo)
    }

    @Test
    fun testLoginResponseSerialization() {
        val json = """
            {
                "token": "eyJhbGciOiJIUzI1NiJ9.testToken",
                "usuario": {
                    "id": 1,
                    "nombre": "Rubiel",
                    "apellido": "Diaz",
                    "numeroDocumento": "1001",
                    "email": "rubiel@example.com",
                    "usuario": "rubiel",
                    "idRol": 1,
                    "activo": 1
                }
            }
        """.trimIndent()

        val loginResponse = gson.fromJson(json, LoginResponse::class.java)

        assertNotNull(loginResponse)
        assertEquals("eyJhbGciOiJIUzI1NiJ9.testToken", loginResponse.token)
        assertEquals("Rubiel", loginResponse.usuario.nombre)
        assertEquals(1L, loginResponse.usuario.idRol)
        assertEquals(1L, loginResponse.usuario.id)
    }

    @Test
    fun testMovimientoRequestSerialization() {
        val movimiento = MovimientoRequest(
            idProducto = 1L,
            tipoMovimiento = "ENTRADA",
            cantidad = 20,
            referencia = "LOT-2026-001",
            observacion = "Compra proveedor",
            idUsuario = 1L
        )

        val json = gson.toJson(movimiento)
        assertTrue(json.contains("\"idProducto\":1"))
        assertTrue(json.contains("\"tipoMovimiento\":\"ENTRADA\""))
        assertTrue(json.contains("\"cantidad\":20"))
        assertTrue(json.contains("\"referencia\":\"LOT-2026-001\""))
    }
}
