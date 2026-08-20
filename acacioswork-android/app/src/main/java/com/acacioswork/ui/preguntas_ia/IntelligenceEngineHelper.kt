package com.acacioswork.ui.preguntas_ia

import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Objects

/**
 * Métodos analíticos adicionales de soporte para el motor de Inteligencia de Negocios en Android.
 * @author RADJ / Antigravity
 */
object IntelligenceEngineHelper {

    fun filtrarVentasPorRango(ventas: List<Venta>, dateFrom: String, dateTo: String): List<Venta> {
        if (dateFrom.isBlank() || dateTo.isBlank()) return ventas
        return ventas.filter { v ->
            v.fechaHora?.let { dateStr ->
                try {
                    val dateOnly = dateStr.split("T")[0]
                    dateOnly in dateFrom..dateTo
                } catch (e: Exception) {
                    false
                }
            } ?: false
        }
    }

    fun iqAnalizarMejorMes(productos: List<Producto>, ventasAll: List<Venta>): String {
        if (productos.isEmpty() || ventasAll.isEmpty()) return "No hay historial de ventas para analizar."
        val prodMap = productos.associateBy { it.id }

        class MesGanancia(val label: String, var ganancia: Double = 0.0)
        val monthNames = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        val gananciasMap = mutableMapOf<String, MesGanancia>()

        for (v in ventasAll) {
            val fh = v.fechaHora ?: continue
            val detalles = v.detalles
            try {
                val date = LocalDateTime.parse(fh)
                val key = "${date.year}-${String.format("%02d", date.monthValue)}"
                val label = "${monthNames[date.monthValue - 1]} ${date.year}"

                val mg = gananciasMap.getOrPut(key) { MesGanancia(label) }
                for (d in detalles) {
                    val p = prodMap[d.idProducto]
                    val costo = p?.precioCompra ?: 0.0
                    mg.ganancia += (d.precioUnitario - costo) * d.cantidad
                }
            } catch (e: Exception) {
                // Formato no parseable
            }
        }

        val sortedList = gananciasMap.values.sortedByDescending { it.ganancia }
        if (sortedList.isEmpty()) return "No hay historial de ventas para analizar."

        val sb = StringBuilder()
        var count = 1
        for (mg in sortedList) {
            if (count > 3) break
            sb.append("$count. ${mg.label} → Ganancia: $${String.format("%,.0f", mg.ganancia)}\n")
            count++
        }
        return sb.toString().trim()
    }

    fun iqAnalizarPerdidas(productos: List<Producto>): String {
        if (productos.isEmpty()) return "Sin catálogo de productos."
        val ranking = productos.filter { p ->
            p.estado == 1 && p.precioCompra > 0 && p.precioVenta < p.precioCompra
        }.sortedBy { p ->
            p.precioVenta - p.precioCompra
        }.take(3)

        if (ranking.isEmpty()) return "¡Bien! Ningún producto tiene precio de venta inferior al costo."

        val sb = StringBuilder()
        for ((index, p) in ranking.withIndex()) {
            val loss = p.precioCompra - p.precioVenta
            sb.append("${index + 1}. ${p.nombre} → Pérdida: $${String.format("%,.0f", loss)} por unidad\n")
        }
        return sb.toString().trim()
    }

    fun iqAnalizarSinVender(productos: List<Producto>, ventasList: List<Venta>): String {
        if (productos.isEmpty()) return "Sin catálogo de productos."
        val vendidos = mutableSetOf<Long>()
        for (v in ventasList) {
            val detalles = v.detalles
            for (d in detalles) vendidos.add(d.idProducto)
        }

        val sinVender = productos.filter { p ->
            p.estado == 1 && !vendidos.contains(p.id)
        }.take(3)

        if (sinVender.isEmpty()) return "¡Excelente! Todos los productos activos tuvieron ventas en este periodo."

        val sb = StringBuilder()
        sinVender.forEachIndexed { i, p ->
            sb.append("${i + 1}. ${p.nombre} → Sin movimientos de salida\n")
        }
        return sb.toString().trim()
    }

    fun iqAnalizarProximosVencer(productos: List<Producto>): String {
        if (productos.isEmpty()) return "Sin catálogo de productos."
        val today = LocalDate.now()
        
        class Temp(val n: String, val f: String, val d: Long)
        
        val list = productos.mapNotNull { p ->
            val fv = p.fechaVencimiento
            if (!fv.isNullOrBlank() && fv != "—") {
                try {
                    val exp = LocalDate.parse(fv)
                    Temp(p.nombre, fv, ChronoUnit.DAYS.between(today, exp))
                } catch (e: Exception) {
                    null
                }
            } else null
        }.sortedBy { it.d }.take(4)

        if (list.isEmpty()) return "No hay productos con fecha de vencimiento registrada."

        val sb = StringBuilder()
        list.forEachIndexed { i, t ->
            val lbl = if (t.d < 0) "(Vencido hace ${Math.abs(t.d)}d)"
            else if (t.d == 0L) "(Vence HOY)"
            else if (t.d == 1L) "(Vence Mañana)"
            else "(Vence en ${t.d}d)"
            sb.append("${i + 1}. ${t.n} → ${t.f} $lbl\n")
        }
        return sb.toString().trim()
    }
}
