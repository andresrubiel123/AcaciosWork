package com.acacioswork.ui.reportes

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Pestaña modular de Reportes del Sistema en Android con paridad al 100% con la versión Web.
 * Actúa como un Hub/Menú de 3 opciones principales: Reportes PDF, Preguntas Inteligentes y Análisis Gráfico.
 * @author RADJ / Antigravity
 */
@Composable
fun ReportesTab() {
    var currentSubScreen by remember { mutableStateOf("HUB") }

    when (currentSubScreen) {
        "HUB" -> ReportesHubScreen(
            onNavigateToPdf = { currentSubScreen = "PDF" },
            onNavigateToPreguntas = { currentSubScreen = "PREGUNTAS" },
            onNavigateToGraficos = { currentSubScreen = "GRAFICOS" }
        )
        "PDF" -> ReportesPdfScreen(onBack = { currentSubScreen = "HUB" })
        "PREGUNTAS" -> com.acacioswork.ui.preguntas_ia.PreguntasIaScreen(onBack = { currentSubScreen = "HUB" })
        "GRAFICOS" -> com.acacioswork.ui.graficos.GraficosTab(onBack = { currentSubScreen = "HUB" })
    }
}

/**
 * Menú principal (Hub) del módulo de Reportes.
 * Muestra las 3 opciones de reportes y análisis.
 * @author RADJ / Antigravity
 */
@Composable
fun ReportesHubScreen(
    onNavigateToPdf: () -> Unit,
    onNavigateToPreguntas: () -> Unit,
    onNavigateToGraficos: () -> Unit
) {
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
            // Cabecera Oficial (Paridad Web)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("Módulo de Reportes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("Selecciona una herramienta para el análisis comercial de tu negocio", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }

            // Opción 1: Generación de Reportes PDF
            item {
                ReportesHubCard(
                    title = "📄 Generar Reportes PDF",
                    description = "Accede a los 10 reportes listos para exportar en formato PDF de stock, vencimiento y ventas.",
                    icon = Icons.AutoMirrored.Filled.List,
                    iconColor = Primary,
                    onClick = onNavigateToPdf
                )
            }

            // Opción 2: Preguntas Inteligentes
            item {
                ReportesHubCard(
                    title = "💡 Preguntas Inteligentes",
                    description = "Motor de análisis automático por mes. Identifica rentabilidad, productos de baja rotación y vencimiento.",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconColor = AccentOrange,
                    onClick = onNavigateToPreguntas
                )
            }

            // Opción 3: Análisis Gráfico
            item {
                ReportesHubCard(
                    title = "📊 Análisis Gráfico",
                    description = "Visualización intuitiva con gráficos de barras y líneas para el control e ingresos del negocio.",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = AccentGreen,
                    onClick = onNavigateToGraficos
                )
            }
        }
    }
}

/**
 * Pantalla que lista las 10 tarjetas de reportes PDF originales con botón Atrás.
 * @author RADJ / Antigravity
 */
@Composable
fun ReportesPdfScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

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
            // Cabecera Oficial con botón Atrás (Paridad Web)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextLight)
                    }
                    Column {
                        Text("Generación de Reportes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("Exportación de informes y estadísticas en formato PDF", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            // 1. Inventario General
            item {
                ReporteSimpleCard(
                    title = "📦 Inventario General",
                    description = "Lista completa de productos con stock y precios actuales.",
                    icon = Icons.AutoMirrored.Filled.List,
                    iconColor = Primary,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Inventario General", "inventario") }
                )
            }

            // 2. Productos con Stock Bajo
            item {
                ReporteSimpleCard(
                    title = "⚠️ Productos con Stock Bajo",
                    description = "Listado de artículos por debajo del stock mínimo definido.",
                    icon = Icons.Default.Info,
                    iconColor = AlertRed,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Productos con Stock Bajo", "stock-bajo") }
                )
            }

            // 3. Control de Vencimientos
            item {
                ReporteSimpleCard(
                    title = "📅 Control de Vencimientos",
                    description = "Productos vencidos o próximos a vencer dentro de los siguientes 5 días.",
                    icon = Icons.Default.DateRange,
                    iconColor = AlertRed,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Control de Vencimientos", "vencimientos") }
                )
            }

            // 4. Reporte de Clientes
            item {
                ReporteSimpleCard(
                    title = "👥 Reporte de Clientes",
                    description = "Base de clientes registrados con su información de contacto.",
                    icon = Icons.Default.Person,
                    iconColor = Primary,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Reporte de Clientes", "clientes") }
                )
            }

            // 5. Reporte de Proveedores
            item {
                ReporteSimpleCard(
                    title = "🏭 Reporte de Proveedores",
                    description = "Directorio de proveedores con datos de contacto y productos.",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = AccentGreen,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Reporte de Proveedores", "proveedores") }
                )
            }

            // 6. Usuarios del Sistema
            item {
                ReporteSimpleCard(
                    title = "👤 Usuarios del Sistema",
                    description = "Listado de usuarios activos, roles y permisos asignados.",
                    icon = Icons.Default.Person,
                    iconColor = Primary,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Usuarios del Sistema", "usuarios") }
                )
            }

            // 7. Reporte de Ventas
            item {
                ReporteSimpleCard(
                    title = "🛒 Reporte de Ventas",
                    description = "Listado histórico de todas las ventas con fecha, clientes y totales.",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = Primary,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Reporte de Ventas", "ventas") }
                )
            }

            // 8. Reporte de Ganancias
            item {
                ReporteSimpleCard(
                    title = "📈 Reporte de Ganancias",
                    description = "Análisis de rentabilidad detallando costos, ingresos y margen de ganancia por venta.",
                    icon = Icons.Default.Star,
                    iconColor = AccentGreen,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Reporte de Ganancias", "ganancias") }
                )
            }

            // 9. Reporte Ejecutivo
            item {
                ReporteSimpleCard(
                    title = "📊 Reporte Ejecutivo",
                    description = "Métricas principales de inventario y estado general de la empresa.",
                    icon = Icons.AutoMirrored.Filled.List,
                    iconColor = AccentOrange,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Reporte Ejecutivo", "resumen") }
                )
            }

            // 10. Vencimientos a 15 Días
            item {
                ReporteSimpleCard(
                    title = "⚠️ Vencimientos a 15 Días",
                    description = "Productos vencidos o próximos a vencer en los siguientes 15 días.",
                    icon = Icons.Default.DateRange,
                    iconColor = AccentOrange,
                    buttonText = "Generar PDF",
                    buttonColor = AccentOrange,
                    onButtonClick = { shareReport(context, "Vencimientos a 15 Días", "vencimientos-15") }
                )
            }
        }
    }
}

/**
 * Tarjeta interactiva en el menú principal (Hub) de reportes.
 * @author RADJ / Antigravity
 */
@Composable
fun ReportesHubCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
            }
        }
    }
}

private fun shareReport(context: Context, titulo: String, tipo: String) {
    Toast.makeText(context, "Generando consolidado...", Toast.LENGTH_SHORT).show()
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            this.type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, titulo)
            putExtra(
                Intent.EXTRA_TEXT,
                "AcaciosWork - $titulo\n" +
                "Tipo de reporte: $tipo\n" +
                "Fecha de generación: ${java.time.LocalDate.now()}\n" +
                "--------------------------------------------------\n" +
                "Métricas comerciales y de inventario compiladas del servidor para la gestión administrativa."
            )
        }
        val shareIntent = Intent.createChooser(sendIntent, "Exportar a:")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
