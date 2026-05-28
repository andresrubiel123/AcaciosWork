package com.acacioswork.ui.reportes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Categoria
import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Tarjeta de Gráfico para Ventas por Categoría de Producto.
 * Permite filtrar por mes y año y dibuja un gráfico de barras horizontales personalizadas en Canvas.
 * 
 * @author RADJ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasChartCard() {
    val coroutineScope = rememberCoroutineScope()
    
    // Estados de datos
    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var productosMap by remember { mutableStateOf<Map<Long, Producto>>(emptyMap()) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados de filtros (por defecto mes y año actual)
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH) + 1) } // 1-12
    var selectedYearStr by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }
    var expandedMonthDropdown by remember { mutableStateOf(false) }

    val nombresMeses = listOf(
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    // Cargar datos al inicializar
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val responseVentas = RetrofitClient.apiService.getVentas()
            val responseProductos = RetrofitClient.apiService.getProductos()
            val responseCategorias = RetrofitClient.apiService.getCategorias()

            if (responseVentas.success && responseVentas.data != null &&
                responseProductos.success && responseProductos.data != null &&
                responseCategorias.success && responseCategorias.data != null) {
                
                ventas = responseVentas.data
                productosMap = responseProductos.data.associateBy { it.id ?: 0L }
                categorias = responseCategorias.data
            } else {
                errorMessage = responseVentas.message ?: responseProductos.message ?: responseCategorias.message
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Error al conectar con el servidor."
        } finally {
            isLoading = false
        }
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
                    // Formato ISO: YYYY-MM-DD
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
    val totalUnidades = categoryStats.sumOf { it.unidades }
    val totalGanancia = categoryStats.sumOf { it.ganancia }

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
                        .background(AccentGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AccentGreen,
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
                        text = "Comparativa de unidades vendidas y utilidades",
                        fontSize = 12.sp,
                        color = TextMuted
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
                // Dropdown de Mes con fondo negro/oscuro y letras blancas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(15, 23, 42))
                        .clickable { expandedMonthDropdown = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nombresMeses[selectedMonth],
                            color = Color.WHITE,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.WHITE
                        )
                    }

                    DropdownMenu(
                        expanded = expandedMonthDropdown,
                        onDismissRequest = { expandedMonthDropdown = false },
                        modifier = Modifier.background(Color(15, 23, 42))
                    ) {
                        for (i in 1..12) {
                            DropdownMenuItem(
                                text = { Text(nombresMeses[i], color = Color.WHITE) },
                                onClick = {
                                    selectedMonth = i
                                    expandedMonthDropdown = false
                                }
                            )
                        }
                    }
                }

                // Input de Año con fondo negro/oscuro y letras blancas
                OutlinedTextField(
                    value = selectedYearStr,
                    onValueChange = { selectedYearStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(color = Color.WHITE, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(15, 23, 42),
                        unfocusedContainerColor = Color(15, 23, 42),
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color(255, 255, 255, 20)
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RESUMEN DEL PERIODO SELECCIONADO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(15, 23, 42))
                    .padding(12.dp)
            ) {
                Text(
                    text = "RESUMEN DEL PERÍODO SELECCIONADO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val formattedUnidades = String.format("%,d", totalUnidades).replace(',', '.')
                val formattedGanancia = String.format("%,d", totalGanancia.toLong()).replace(',', '.')
                Text(
                    text = "Total Unidades Vendidas en ${nombresMeses[selectedMonth]} $targetYear = $formattedUnidades Unidades  |  Ganancia Total: $ $formattedGanancia",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas del gráfico
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error cargando gráfico",
                        color = AlertRed,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (categoryStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin datos de ventas para ${nombresMeses[selectedMonth]} $targetYear.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val chartHeightDp = (categoryStats.size * 48 + 32).dp
                CategoriasHorizontalBarChart(
                    stats = categoryStats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeightDp)
                )
            }
        }
    }
}

/**
 * Representa una fila en las estadísticas del gráfico.
 * @author RADJ
 */
data class CategoryStatItem(
    val nombre: String,
    val unidades: Int,
    val ganancia: Double
)

/**
 * Gráfico personalizado de barras horizontales dibujado sobre Canvas en Compose.
 * @author RADJ
 */
@Composable
fun CategoriasHorizontalBarChart(
    stats: List<CategoryStatItem>,
    modifier: Modifier = Modifier
) {
    val maxUnidades = stats.maxOfOrNull { it.unidades } ?: 1

    val palette = listOf(
        Color(99, 102, 241),   // Indigo
        Color(139, 92, 246),  // Violet
        Color(59, 130, 246),  // Blue
        Color(16, 185, 129),  // Emerald
        Color(245, 158, 11),  // Amber
        Color(239, 68, 68)    // Red
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val paddingLeft = 100.dp.toPx()
        val paddingRight = 180.dp.toPx() // Espacio para las etiquetas del final de barra
        val paddingTop = 16.dp.toPx()
        val paddingBottom = 16.dp.toPx()

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom

        if (graphWidth <= 0 || graphHeight <= 0) return@Canvas

        val numItems = stats.size
        val barHeight = 24.dp.toPx()
        val gap = (graphHeight - (barHeight * numItems)) / (numItems + 1).coerceAtLeast(1)

        // Pintores para las etiquetas nativas
        val textPaintCategory = android.graphics.Paint().apply {
            color = TextLight.toArgb()
            textSize = 10.dp.toPx()
            typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }

        val textPaintUnidades = android.graphics.Paint().apply {
            color = TextMuted.toArgb()
            textSize = 10.dp.toPx()
            typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaintGanancia = android.graphics.Paint().apply {
            color = AccentGreen.toArgb()
            textSize = 10.dp.toPx()
            typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }

        // Dibujar eje Y
        drawLine(
            color = Color(255, 255, 255, 20),
            start = Offset(paddingLeft, paddingTop),
            end = Offset(paddingLeft, paddingTop + graphHeight),
            strokeWidth = 1.dp.toPx()
        )

        stats.forEachIndexed { index, item ->
            val y = paddingTop + gap + index * (barHeight + gap)
            val centerY = y + (barHeight / 2)

            // 1. Dibujar nombre de la categoría
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    item.nombre,
                    paddingLeft - 8.dp.toPx(),
                    centerY + 4.dp.toPx(),
                    textPaintCategory
                )
            }

            // 2. Ancho y dibujo de la barra redondeada
            val barWidth = ((item.unidades.toFloat() / maxUnidades) * graphWidth).coerceAtLeast(6.dp.toPx())
            drawRoundRect(
                color = palette[index % palette.size],
                topLeft = Offset(paddingLeft, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )

            // 3. Dibujar etiquetas de datos al final de la barra: "N und.  $ X Ganancia"
            val textX = paddingLeft + barWidth + 8.dp.toPx()
            val formattedUnidades = String.format("%,d", item.unidades).replace(',', '.') + " und.  "
            val formattedGanancia = "$ " + String.format("%,d", item.ganancia.toLong()).replace(',', '.') + " Ganancia"

            drawIntoCanvas { canvas ->
                // Dibujar unidades en gris
                canvas.nativeCanvas.drawText(
                    formattedUnidades,
                    textX,
                    centerY + 4.dp.toPx(),
                    textPaintUnidades
                )

                // Calcular ancho de unidades para desfasar la ganancia en verde
                val undWidth = textPaintUnidades.measureText(formattedUnidades)
                
                // Dibujar ganancia en verde
                canvas.nativeCanvas.drawText(
                    formattedGanancia,
                    textX + undWidth,
                    centerY + 4.dp.toPx(),
                    textPaintGanancia
                )
            }
        }
    }
}
