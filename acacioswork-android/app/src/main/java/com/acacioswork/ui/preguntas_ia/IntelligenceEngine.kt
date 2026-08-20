package com.acacioswork.ui.preguntas_ia

import com.acacioswork.model.Cliente
import com.acacioswork.model.Producto
import com.acacioswork.model.Proveedor
import com.acacioswork.model.Venta
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Motor analítico local para procesar las 9 preguntas de negocio inteligentes en Android.
 * @author RADJ / Antigravity
 */
object IntelligenceEngine {

    fun iqAnalizarRentables(productos: List<Producto>, ventasList: List<Venta>): String {
        if (productos.isEmpty() || ventasList.isEmpty()) return "No se registraron ventas en este periodo."
        val prodMap = productos.associateBy { it.id }
        val gananciaMap = mutableMapOf<Long, Double>()

        for (v in ventasList) {
            val detalles = v.detalles
            for (d in detalles) {
                val p = prodMap[d.idProducto]
                if (p != null) {
                    val margen = (d.precioUnitario - p.precioCompra) * d.cantidad
                    gananciaMap[d.idProducto] = gananciaMap.getOrDefault(d.idProducto, 0.0) + margen
                }
            }
        }

        val sortedList = gananciaMap.entries.sortedByDescending { it.value }
        if (sortedList.isEmpty()) return "No se registraron ventas en este periodo."

        val sb = StringBuilder()
        var count = 1
        for (entry in sortedList) {
            if (count > 3) break
            val p = prodMap[entry.key]
            val name = p?.nombre ?: "Producto #${entry.key}"
            sb.append("$count. $name → Ganancia: $${String.format("%,.0f", entry.value)}\n")
            count++
        }
        return sb.toString().trim()
    }

    fun iqAnalizarBajaRotacion(productos: List<Producto>, ventasList: List<Venta>): String {
        if (productos.isEmpty()) return "Sin catálogo de productos."
        val cantidadMap = productos.filter { it.estado == 1 }.associate { it.id to 0 }.toMutableMap()

        for (v in ventasList) {
            val detalles = v.detalles
            for (d in detalles) {
                if (cantidadMap.containsKey(d.idProducto)) {
                    cantidadMap[d.idProducto] = cantidadMap.getOrDefault(d.idProducto, 0) + d.cantidad
                }
            }
        }

        val sortedList = cantidadMap.entries.filter { it.value > 0 }.sortedBy { it.value }
        if (sortedList.isEmpty()) return "No hay productos con ventas en este periodo."

        val prodMap = productos.associateBy { it.id }
        val sb = StringBuilder()
        var count = 1
        for (entry in sortedList) {
            if (count > 3) break
            val p = prodMap[entry.key]
            val name = p?.nombre ?: "Producto #${entry.key}"
            sb.append("$count. $name → Solo ${entry.value} uds vendidas\n")
            count++
        }
        return sb.toString().trim()
    }

    fun iqAnalizarReabastecer(productos: List<Producto>): String {
        if (productos.isEmpty()) return "Sin catálogo de productos."
        val ranking = productos.filter { p ->
            p.estado == 1 && p.stockActual <= p.stockMinimo
        }.sortedBy { p ->
            p.stockActual - p.stockMinimo
        }.take(3)

        if (ranking.isEmpty()) return "¡Excelente! Todos los productos tienen stock suficiente."

        val sb = StringBuilder()
        for ((index, p) in ranking.withIndex()) {
            sb.append("${index + 1}. ${p.nombre} → Stock: ${p.stockActual} uds (mín: ${p.stockMinimo})\n")
        }
        return sb.toString().trim()
    }

    fun iqAnalizarProveedorCaro(productos: List<Producto>, proveedores: List<Proveedor>): String {
        if (productos.isEmpty() || proveedores.isEmpty()) return "Sin datos suficientes."
        
        class Stats(var total: Double = 0.0, var count: Int = 0)
        val provMap = proveedores.associateBy { it.id }
        val statsMap = mutableMapOf<Long, Stats>()

        productos.forEach { p ->
            val pid = p.idProveedor
            if (pid != null) {
                val st = statsMap.getOrPut(pid) { Stats() }
                st.total += p.precioCompra
                st.count++
            }
        }

        val list = statsMap.mapNotNull { (pid, st) ->
            val p = provMap[pid]
            if (p != null && st.count > 0) {
                val avg = st.total / st.count
                Pair(p.nombre, avg)
            } else null
        }.sortedByDescending { it.second }.take(3)

        if (list.isEmpty()) return "Sin asignación de proveedores."

        val sb = StringBuilder()
        list.forEachIndexed { i, pair ->
            sb.append("${i + 1}. ${pair.first} → Costo Promedio: $${String.format("%,.0f", pair.second)}\n")
        }
        return sb.toString().trim()
    }

    fun iqAnalizarMejoresClientes(ventasList: List<Venta>, clientes: List<Cliente>): String {
        if (ventasList.isEmpty()) return "No hay ventas registradas."
        val clientMap = clientes.associateBy { it.id }
        val comprasMap = mutableMapOf<Long, Double>()

        ventasList.forEach { v ->
            val cid = v.idCliente ?: 0L // 0 = Genérico
            var total = v.valorTotal
            if (total == 0.0 && v.detalles.isNotEmpty()) {
                total = v.detalles.sumOf { it.cantidad * it.precioUnitario }
            }
            comprasMap[cid] = comprasMap.getOrDefault(cid, 0.0) + total
        }

        val list = comprasMap.entries.sortedByDescending { it.value }.take(3)
        val sb = StringBuilder()
        list.forEachIndexed { i, entry ->
            val c = clientMap[entry.key]
            val name = c?.nombre ?: if (entry.key == 0L) "Cliente Genérico" else "Cliente #${entry.key}"
            sb.append("${i + 1}. $name → Total Compras: $${String.format("%,.0f", entry.value)}\n")
        }
        return sb.toString().trim()
    }

    fun iqAnalizarTopClientes(clientes: List<Cliente>, ventasList: List<Venta>): String {
        return iqAnalizarMejoresClientes(ventasList, clientes)
    }

    fun iqAnalizarMetasVentas(ventasList: List<Venta>): String {
        if (ventasList.isEmpty()) return "No hay ventas en este periodo."
        val total = ventasList.sumOf { v ->
            var totalVal = v.valorTotal
            if (totalVal == 0.0 && v.detalles.isNotEmpty()) {
                totalVal = v.detalles.sumOf { it.cantidad * it.precioUnitario }
            }
            totalVal
        }

        val meta = 5000000.0 // Meta mensual de 5M
        val pct = (total / meta) * 100.0
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("es", "ES"))
        val mesActual = LocalDate.now().format(formatter)

        return "Meta de Ventas Mensual ($mesActual):\n" +
                "• Logrado: $${String.format("%,.0f", total)} / Meta: $${String.format("%,.0f", meta)}\n" +
                "• Progreso: ${String.format("%.1f", pct)}%"
    }
}
