package com.acacioswork.ui.reportes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.ui.theme.*

@Composable
fun ReportesTab(
    viewModel: ReportesViewModel = viewModel()
) {
    val salesData by viewModel.monthlySales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarVentas()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Título de la pestaña
            item {
                Text(
                    text = "Reportes del Sistema",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = "Generación y visualización de análisis de inventario",
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tarjeta de Reporte de Inventario (Con Imagen)
            item {
                ReporteImagenCard(
                    title = "Reporte de Inventario",
                    description = "Consolidado de stock actual, valorización de inventario a precio de compra y venta, y porcentaje de IVA.",
                    imageResId = com.acacioswork.R.drawable.inventario,
                    buttonText = "Generar Reporte de Inventario",
                    onButtonClick = {
                        // Acción para generar reporte
                    }
                )
            }

            // Tarjeta de Reporte de Clientes
            item {
                ReporteSimpleCard(
                    title = "Análisis de Clientes",
                    description = "Frecuencia de compra de clientes, historial de visitas y listado de clientes frecuentes activos.",
                    icon = Icons.Default.Person,
                    iconColor = Primary,
                    buttonText = "Generar Reporte de Clientes",
                    onButtonClick = {}
                )
            }

            // Tarjeta de Reporte de Proveedores
            item {
                ReporteSimpleCard(
                    title = "Consolidado de Proveedores",
                    description = "Cuentas bancarias de proveedores registrados, números de documento, teléfonos y correos electrónicos.",
                    icon = Icons.Default.Info,
                    iconColor = AccentGreen,
                    buttonText = "Generar Reporte de Proveedores",
                    onButtonClick = {}
                )
            }

            // Tarjeta de Alertas y Stock Mínimo
            item {
                ReporteSimpleCard(
                    title = "Alertas de Stock Crítico",
                    description = "Visualización detallada de todos los productos que están igual o por debajo del stock mínimo configurado.",
                    icon = Icons.Default.Warning,
                    iconColor = AlertRed,
                    buttonText = "Generar Alertas Críticas",
                    onButtonClick = {}
                )
            }

            // Tarjeta de Reporte de Ventas
            item {
                ReporteSimpleCard(
                    title = "Reporte de Ventas",
                    description = "Listado histórico de todas las ventas con fecha, clientes y totales.",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = Primary,
                    buttonText = "Generar Reporte de Ventas",
                    buttonColor = Primary,
                    onButtonClick = {}
                )
            }

            // Tarjeta de Reporte de Ganancias
            item {
                ReporteSimpleCard(
                    title = "Reporte de Ganancias",
                    description = "Análisis de rentabilidad detallando costos, ingresos y margen de ganancia por venta.",
                    icon = Icons.Default.Star,
                    iconColor = AccentGreen,
                    buttonText = "Generar Reporte de Ganancias",
                    buttonColor = AccentGreen,
                    onButtonClick = {}
                )
            }

            // Tarjeta de Reporte Ejecutivo
            item {
                ReporteSimpleCard(
                    title = "Reporte Ejecutivo",
                    description = "Métricas principales de inventario y estado general de la empresa.",
                    icon = Icons.Default.List,
                    iconColor = AccentOrange,
                    buttonText = "Generar Reporte Ejecutivo",
                    buttonColor = AccentOrange,
                    onButtonClick = {}
                )
            }

            // Gráfico de Tendencia de Ventas Mensuales
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "📈 Tendencia de Ventas Mensuales",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Text(
                                    text = "Ingresos mensuales (año actual)",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary)
                            }
                        } else if (errorMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = errorMessage ?: "Error desconocido",
                                    color = AlertRed,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            VentasLineChart(salesData = salesData)
                        }
                    }
                }
            }
        }
    }
}

/** VentasLineChart draws a custom line chart showing monthly sales trends. @author RADJ */
@Composable
fun VentasLineChart(salesData: List<Double>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val width = size.width
        val height = size.height

        // Márgenes en píxeles usando dp.toPx()
        val paddingLeft = 70.dp.toPx()
        val paddingRight = 15.dp.toPx()
        val paddingTop = 35.dp.toPx()
        val paddingBottom = 30.dp.toPx()

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom

        // Determinar el valor máximo para escalar el eje Y (mínimo de $50,000)
        val maxVal = maxOf(50000.0, salesData.maxOrNull() ?: 50000.0)

        // Número de divisiones para la rejilla
        val numDivisions = 5
        val divisionStepVal = maxVal / numDivisions

        // Dibujar "COP" una sola vez en la parte superior del eje Y. @author RADJ
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

        // Formateador para los valores reales del eje Y
        val formatterY = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }

        // Dibujar rejilla horizontal y etiquetas del eje Y
        for (i in 0..numDivisions) {
            val currentVal = i * divisionStepVal
            val y = paddingTop + graphHeight - ((currentVal / maxVal) * graphHeight).toFloat()

            // Línea guía de rejilla horizontal
            if (i > 0) {
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(paddingLeft, y),
                    end = Offset(paddingLeft + graphWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Etiqueta formateada con valor real sin dólar ni K/M. @author RADJ
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
                    typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
                }
            )
        }

        // Dibujar rejilla vertical y etiquetas del eje X (Meses)
        val stepX = graphWidth / 11f
        val pointXs = FloatArray(12)
        val pointYs = FloatArray(12)
        val months = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

        for (i in 0 until 12) {
            val x = paddingLeft + i * stepX
            pointXs[i] = x
            pointYs[i] = paddingTop + graphHeight - ((salesData[i] / maxVal) * graphHeight).toFloat()

            // Línea de rejilla vertical
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(x, paddingTop),
                end = Offset(x, paddingTop + graphHeight),
                strokeWidth = 1.dp.toPx()
            )

            // Nombre del mes
            drawContext.canvas.nativeCanvas.drawText(
                months[i],
                x,
                paddingTop + graphHeight + 18.dp.toPx(),
                android.graphics.Paint().apply {
                    color = TextMuted.toArgb()
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
                }
            )
        }

        // Eje X principal
        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(paddingLeft, paddingTop + graphHeight),
            end = Offset(paddingLeft + graphWidth, paddingTop + graphHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Crear el trazado de la línea con curvas Bezier
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

        // Rellenar área inferior del degradado
        val fillPath = Path().apply {
            addPath(path)
            lineTo(pointXs[11], paddingTop + graphHeight)
            lineTo(pointXs[0], paddingTop + graphHeight)
            close()
        }

        // Relleno degradado Indigo/morado (0xFF6366F1) a transparente
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF6366F1).copy(alpha = 0.18f),
                    Color(0xFF6366F1).copy(alpha = 0.0f)
                ),
                startY = paddingTop,
                endY = paddingTop + graphHeight
            )
        )

        // Dibujar la línea principal (Indigo 0xFF6366F1)
        drawPath(
            path = path,
            color = Color(0xFF6366F1),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Dibujar los puntos resaltados
        for (i in 0 until 12) {
            val x = pointXs[i]
            val y = pointYs[i]
            val value = salesData[i]

            // Círculo blanco exterior
            drawCircle(
                color = Color.White,
                radius = 4.5.dp.toPx(),
                center = Offset(x, y)
            )

            // Círculo interior naranja (0xFFF97316)
            drawCircle(
                color = Color(0xFFF97316),
                radius = 2.5.dp.toPx(),
                center = Offset(x, y)
            )

            // Etiqueta de valor del punto si es mayor a cero
            if (value > 0.0) {
                // Formateador con el valor real sin el símbolo de dólar ni K/M. @author RADJ
                val formatterPoint = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "CO")).apply {
                    maximumFractionDigits = 0
                }
                val valStr = formatterPoint.format(value)

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


@Composable
fun ReporteImagenCard(
    title: String,
    description: String,
    imageResId: Int,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Textos descriptivos alineados a la izquierda
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del Reporte - Centrada, Escalada y Redondeada sin tapar el botón
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Visualización de Reporte",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón en la parte inferior con suficiente espaciado de separación
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
        }
    }
}

@Composable
fun ReporteSimpleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    buttonText: String,
    buttonColor: androidx.compose.ui.graphics.Color = BgDark,
    onButtonClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
        }
    }
}
