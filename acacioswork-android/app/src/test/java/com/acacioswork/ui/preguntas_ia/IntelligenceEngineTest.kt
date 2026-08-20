package com.acacioswork.ui.preguntas_ia

import com.acacioswork.model.Cliente
import com.acacioswork.model.DetalleVenta
import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import org.junit.Assert.*
import org.junit.Test

class IntelligenceEngineTest {

    @Test
    fun testIqAnalizarReabastecerConProductosCriticos() {
        val productos = listOf(
            Producto(id = 1, nombre = "Arroz", stockActual = 2, stockMinimo = 5, precioCompra = 2000.0, precioVenta = 3000.0, iva = 19.0, estado = 1),
            Producto(id = 2, nombre = "Leche", stockActual = 20, stockMinimo = 5, precioCompra = 1500.0, precioVenta = 2200.0, iva = 0.0, estado = 1),
            Producto(id = 3, nombre = "Aceite", stockActual = 0, stockMinimo = 3, precioCompra = 5000.0, precioVenta = 7500.0, iva = 19.0, estado = 1)
        )

        val resultado = IntelligenceEngine.iqAnalizarReabastecer(productos)

        assertTrue(resultado.contains("Aceite"))
        assertTrue(resultado.contains("Arroz"))
        assertFalse(resultado.contains("Leche"))
    }

    @Test
    fun testIqAnalizarReabastecerSinProductosCriticos() {
        val productos = listOf(
            Producto(id = 1, nombre = "Arroz", stockActual = 10, stockMinimo = 5, precioCompra = 2000.0, precioVenta = 3000.0, iva = 19.0, estado = 1)
        )

        val resultado = IntelligenceEngine.iqAnalizarReabastecer(productos)

        assertEquals("¡Excelente! Todos los productos tienen stock suficiente.", resultado)
    }

    @Test
    fun testIqAnalizarRentables() {
        val p1 = Producto(id = 1, nombre = "Arroz 1kg", stockActual = 50, stockMinimo = 5, precioCompra = 2000.0, precioVenta = 3000.0, iva = 0.0)
        val p2 = Producto(id = 2, nombre = "Café 500g", stockActual = 30, stockMinimo = 5, precioCompra = 5000.0, precioVenta = 9000.0, iva = 0.0)

        val v1 = Venta(
            id = 101,
            detalles = listOf(
                DetalleVenta(id = 1, idProducto = 1, cantidad = 5, precioUnitario = 3000.0), // Ganancia = (3000-2000)*5 = 5000
                DetalleVenta(id = 2, idProducto = 2, cantidad = 2, precioUnitario = 9000.0)  // Ganancia = (9000-5000)*2 = 8000
            )
        )

        val resultado = IntelligenceEngine.iqAnalizarRentables(listOf(p1, p2), listOf(v1))

        assertTrue(resultado.contains("Café 500g"))
        assertTrue(resultado.contains("Arroz 1kg"))
    }

    @Test
    fun testIqAnalizarMejoresClientes() {
        val c1 = Cliente(id = 1, numeroDocumento = "1010", nombre = "Maria Lopez")
        val c2 = Cliente(id = 2, numeroDocumento = "2020", nombre = "Pedro Gomez")

        val v1 = Venta(id = 1, idCliente = 1, valorTotal = 150000.0)
        val v2 = Venta(id = 2, idCliente = 2, valorTotal = 450000.0)

        val resultado = IntelligenceEngine.iqAnalizarMejoresClientes(listOf(v1, v2), listOf(c1, c2))

        assertTrue(resultado.contains("1. Pedro Gomez"))
        assertTrue(resultado.contains("2. Maria Lopez"))
    }

    @Test
    fun testIqAnalizarMetasVentas() {
        val v1 = Venta(id = 1, valorTotal = 2500000.0)
        val v2 = Venta(id = 2, valorTotal = 1000000.0)

        val resultado = IntelligenceEngine.iqAnalizarMetasVentas(listOf(v1, v2))

        assertTrue(resultado.contains("Meta de Ventas Mensual"))
        assertTrue(resultado.contains("70,0%") || resultado.contains("70.0%"))
    }
}
