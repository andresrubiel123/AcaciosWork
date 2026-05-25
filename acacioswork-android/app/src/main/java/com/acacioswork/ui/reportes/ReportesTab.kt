package com.acacioswork.ui.reportes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

@Composable
fun ReportesTab() {
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
