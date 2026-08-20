package com.acacioswork.ui.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.acacioswork.model.Categoria
import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import java.util.Calendar

/**
 * Tarjeta de Gráfico para Ventas por Categoría de Producto con paridad Web al 100%.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasChartCard() {
    val context = LocalContext.current
    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var productosMap by remember { mutableStateOf<Map<Long, Producto>>(emptyMap()) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados de filtros (por defecto mes y año actual)
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH) + 1) } // 1-12
    var selectedYearStr by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }

    val nombresMeses = listOf(
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    // Cargar datos al inicializar
    LaunchedEffect(Unit) {
        isLoading = true; errorMessage = null
        try {
            val resV = RetrofitClient.apiService.getVentas()
            val resP = RetrofitClient.apiService.getProductos()
            val resC = RetrofitClient.apiService.getCategorias()
            if (resV.success && resP.success && resC.success) {
                ventas = resV.data ?: emptyList()
                productosMap = (resP.data ?: emptyList()).associateBy { it.id ?: 0L }
                categorias = resC.data ?: emptyList()
            } else { errorMessage = if (!resV.success) resV.message else if (!resP.success) resP.message else resC.message }
        } catch (e: Exception) { errorMessage = "Error al conectar con el servidor." }
        finally { isLoading = false }
    }

    // Calcular estadísticas basadas en el período seleccionado
    val targetYear = selectedYearStr.toIntOrNull() ?: calendar.get(Calendar.YEAR)
    
    val categoryStats = remember(ventas, productosMap, categorias, selectedMonth, targetYear) {
        val stats = mutableMapOf<Long, Pair<String, Int>>() // idCategoria -> Pair(nombre, unidades)
        val gains = mutableMapOf<Long, Double>() // idCategoria -> ganancia

        // Inicializar con todas las categorías
        categorias.forEach { cat ->
            cat.id?.let { cid ->
                stats[cid] = Pair(cat.nombre, 0)
                gains[cid] = 0.0
            }
        }

        ventas.forEach { v ->
            v.fechaHora?.let { dateStr ->
                try {
                    val parts = dateStr.split("T")
                    if (parts.isNotEmpty()) {
                        val dateParts = parts[0].split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt()
                            if (year == targetYear && month == selectedMonth) {
                                v.detalles.forEach { d ->
                                    val prod = productosMap[d.idProducto]
                                    if (prod != null) {
                                        val cid = prod.idCategoria
                                        if (cid != null) {
                                            val current = stats[cid] ?: Pair(prod.unidadMedida, 0)
                                            stats[cid] = Pair(current.first, current.second + d.cantidad)

                                            val profit = (d.precioUnitario - prod.precioCompra) * d.cantidad
                                            gains[cid] = (gains[cid] ?: 0.0) + profit
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Mapear, filtrar los que tengan ventas > 0 y ordenar de mayor a menor unidades vendidas
        stats.mapNotNull { (cid, pair) ->
            if (pair.second > 0) {
                CategoryStatItem(
                    nombre = pair.first,
                    unidades = pair.second,
                    ganancia = gains[cid] ?: 0.0
                )
            } else null
        }.sortedByDescending { it.unidades }
    }

    // Totales del período
    val totalGanancia = categoryStats.sumOf { it.ganancia }
    val estrella = categoryStats.maxByOrNull { it.ganancia }

    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabecera del gráfico
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "📊 Ventas por Categoría de Producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = "Compara el rendimiento de los diferentes tipos de productos que vendes. Identifica cuáles son tus categorías estrella y cuáles necesitan atención.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros: Mes y Año
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown de Mes (Modularizado)
                MonthDropdown(
                    selectedMonth = selectedMonth,
                    onMonthSelected = { selectedMonth = it },
                    nombresMeses = nombresMeses,
                    modifier = Modifier.weight(1f)
                )

                // Input de Año
                OutlinedTextField(
                    value = selectedYearStr,
                    onValueChange = { selectedYearStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(15, 23, 42), unfocusedContainerColor = Color(15, 23, 42), focusedBorderColor = Primary, unfocusedBorderColor = Color(255, 255, 255, 20)),
                    modifier = Modifier.width(100.dp).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RESUMEN DEL PERÍODO SELECCIONADO
            val resumenTexto = if (estrella != null) {
                val formattedEstrellaG = String.format("%,d", estrella.ganancia.toLong()).replace(',', '.')
                val formattedTotalG = String.format("%,d", totalGanancia.toLong()).replace(',', '.')
                "Categoría estrella: ${estrella.nombre} con $ $formattedEstrellaG en utilidades. Ganancia total del mes: $ $formattedTotalG"
            } else {
                "No hay ventas registradas para este período."
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    .background(Color(0xFF6366F1).copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .background(Color(0xFF6366F1))
                )
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "RESUMEN DEL PERÍODO SELECCIONADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = resumenTexto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas del gráfico
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "Error cargando gráfico", color = AlertRed, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            } else if (categoryStats.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Sin datos de ventas para ${nombresMeses[selectedMonth]} $targetYear.", color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            } else {
                val chartHeightDp = (categoryStats.size * 48 + 32).dp
                CategoriasHorizontalBarChart(stats = categoryStats, modifier = Modifier.fillMaxWidth().height(chartHeightDp))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val names = nombresMeses
                        var rText = "AcaciosWork - Ventas por Categoría (${names.getOrNull(selectedMonth)} $targetYear)\n" +
                                "Fecha de generación: ${java.time.LocalDate.now()}\n" +
                                "--------------------------------------------------\n"
                        categoryStats.forEach { rText += "${it.nombre}: ${it.unidades} unidades, Ganancia: ${com.acacioswork.util.ConfigManager.formatCurrency(it.ganancia)}\n" }
                        rText += "--------------------------------------------------\nTotal: ${com.acacioswork.util.ConfigManager.formatCurrency(totalGanancia)}"
                        com.acacioswork.util.ReportSharing.shareReportText(context, "Reporte Categorias", rText)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Generar PDF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
