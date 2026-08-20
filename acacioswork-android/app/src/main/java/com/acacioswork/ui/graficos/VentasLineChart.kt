package com.acacioswork.ui.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Dibuja un gráfico lineal curvo y detallado de las ventas en Compose Canvas.
 * @author RADJ / Antigravity
 */
@Composable
fun VentasLineChart(salesData: List<Double>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 85.dp.toPx()
        val paddingRight = 15.dp.toPx()
        val paddingTop = 35.dp.toPx()
        val paddingBottom = 30.dp.toPx()

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom
        val maxVal = maxOf(50000.0, salesData.maxOrNull() ?: 50000.0)

        val numDivisions = 5
        val divisionStepVal = maxVal / numDivisions

        drawContext.canvas.nativeCanvas.drawText(
            "COP",
            paddingLeft - 8.dp.toPx(),
            paddingTop - 12.dp.toPx(),
            android.graphics.Paint().apply {
                color = TextMuted.toArgb()
                textSize = 9.sp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
                typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
            }
        )

        val formatterY = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }

        for (i in 0..numDivisions) {
            val currentVal = i * divisionStepVal
            val y = paddingTop + graphHeight - ((currentVal / maxVal) * graphHeight).toFloat()

            if (i > 0) {
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(paddingLeft, y),
                    end = Offset(paddingLeft + graphWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val labelStr = formatterY.format(currentVal)
            drawContext.canvas.nativeCanvas.drawText(
                labelStr,
                paddingLeft - 8.dp.toPx(),
                y + 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = TextMuted.toArgb()
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
            )
        }

        val stepX = graphWidth / 11f
        val pointXs = FloatArray(12)
        val pointYs = FloatArray(12)
        val months = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

        for (i in 0 until 12) {
            val x = paddingLeft + i * stepX
            pointXs[i] = x
            pointYs[i] = paddingTop + graphHeight - ((salesData[i] / maxVal) * graphHeight).toFloat()

            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(x, paddingTop),
                end = Offset(x, paddingTop + graphHeight),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.drawText(
                months[i],
                x,
                paddingTop + graphHeight + 18.dp.toPx(),
                android.graphics.Paint().apply {
                    color = TextMuted.toArgb()
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }

        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(paddingLeft, paddingTop + graphHeight),
            end = Offset(paddingLeft + graphWidth, paddingTop + graphHeight),
            strokeWidth = 1.dp.toPx()
        )

        val path = Path()
        path.moveTo(pointXs[0], pointYs[0])
        for (i in 1 until 12) {
            val prevX = pointXs[i - 1]
            val prevY = pointYs[i - 1]
            val currX = pointXs[i]
            val currY = pointYs[i]
            val ctrlX1 = prevX + (currX - prevX) / 2f
            val ctrlY1 = prevY
            val ctrlX2 = prevX + (currX - prevX) / 2f
            val ctrlY2 = currY
            path.cubicTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, currX, currY)
        }

        val fillPath = Path().apply {
            addPath(path)
            lineTo(pointXs[11], paddingTop + graphHeight)
            lineTo(pointXs[0], paddingTop + graphHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF6366F1).copy(alpha = 0.18f), Color(0xFF6366F1).copy(alpha = 0.0f)),
                startY = paddingTop,
                endY = paddingTop + graphHeight
            )
        )

        drawPath(
            path = path,
            color = Color(0xFF6366F1),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        for (i in 0 until 12) {
            val x = pointXs[i]
            val y = pointYs[i]
            val value = salesData[i]

            drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = Offset(x, y))
            drawCircle(color = Color(0xFFF97316), radius = 2.5.dp.toPx(), center = Offset(x, y))

            if (value > 0.0) {
                val valStr = formatterY.format(value)
                drawContext.canvas.nativeCanvas.drawText(
                    valStr,
                    x,
                    y - 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = Color(0xFFFB923C).toArgb()
                        textSize = 8.5.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
                    }
                )
            }
        }
    }
}
