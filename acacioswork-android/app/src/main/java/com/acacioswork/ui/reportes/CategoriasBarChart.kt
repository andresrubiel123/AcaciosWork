package com.acacioswork.ui.reportes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Representa una fila en las estadísticas del gráfico.
 * @author RADJ / Antigravity
 */
data class CategoryStatItem(
    val nombre: String,
    val unidades: Int,
    val ganancia: Double
)

/**
 * Gráfico personalizado de barras horizontales dibujado sobre Canvas en Compose.
 * @author RADJ / Antigravity
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

/**
 * Dropdown reutilizable para la seleccion de mes con estilo Material 3.
 * @author RADJ / Antigravity
 */
@androidx.compose.runtime.Composable
fun MonthDropdown(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    nombresMeses: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(15, 23, 42))
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nombresMeses[selectedMonth],
                color = Color.White,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(15, 23, 42))
        ) {
            for (i in 1..12) {
                DropdownMenuItem(
                    text = { Text(nombresMeses[i], color = Color.White) },
                    onClick = {
                        onMonthSelected(i)
                        expanded = false
                    }
                )
            }
        }
    }
}

