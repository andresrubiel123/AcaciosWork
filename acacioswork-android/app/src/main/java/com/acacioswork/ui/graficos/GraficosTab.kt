package com.acacioswork.ui.graficos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.reportes.CategoriasChartCard
import com.acacioswork.ui.theme.*
import androidx.compose.ui.platform.LocalContext

/**
 * Pantalla modular para la visualización de gráficos estadísticos en Android
 * con paridad al 100% con la versión Web.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraficosTab(
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    // Carga local de datos para filtrado reactivo de año
    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedYearStr by remember { mutableStateOf("2026") }
    var expandedYearDropdown by remember { mutableStateOf(false) }
    val yearsList = listOf("2026", "2025", "2024", "2023")

    LaunchedEffect(Unit) {
        try {
            val resVentas = RetrofitClient.apiService.getVentas()
            val resProductos = RetrofitClient.apiService.getProductos()
            if (resVentas.success && resVentas.data != null && resProductos.success && resProductos.data != null) {
                ventas = resVentas.data
                productos = resProductos.data
            } else {
                errorMessage = resVentas.message
            }
        } catch (e: Exception) {
            errorMessage = "Error al conectar con la API de ventas."
        } finally {
            isLoading = false
        }
    }

    // Cálculo reactivo local de ganancias mensuales filtradas por el año seleccionado
    val targetYear = selectedYearStr.toIntOrNull() ?: 2026
    val monthlySalesData = remember(ventas, productos, targetYear) {
        val monthlyData = DoubleArray(12) { 0.0 }
        val prodMap = productos.associateBy { it.id }

        ventas.forEach { v ->
            v.fechaHora?.let { dateStr ->
                try {
                    val parts = dateStr.split("T")
                    if (parts.isNotEmpty()) {
                        val dateParts = parts[0].split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt() - 1 // 1-12 -> 0-11
                            if (year == targetYear && month in 0..11) {
                                var total = v.valorTotal
                                if (total == 0.0 && v.detalles.isNotEmpty()) {
                                    total = v.detalles.sumOf { it.cantidad * it.precioUnitario }
                                }
                                
                                var cost = 0.0
                                v.detalles.forEach { d ->
                                    val prod = prodMap[d.idProducto]
                                    val precioCompra = prod?.precioCompra ?: 0.0
                                    cost += d.cantidad * precioCompra
                                }
                                
                                val ganancia = total - cost
                                monthlyData[month] += ganancia
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignorar errores de parsing
                }
            }
        }
        monthlyData.toList()
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
            // Cabecera Oficial (Paridad Web)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = TextLight)
                        }
                    }
                    Column {
                        Text(text = "Análisis Gráficos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text(text = "Rendimiento comercial y análisis de inventario", fontSize = 12.sp, color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 1. Gráfico de Tendencia de Ganancias Mensuales
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
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
                                Text(
                                    text = "📈 Tendencia de Ganancias Mensuales",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            }

                            // Selector de Año
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(15, 23, 42))
                                    .clickable { expandedYearDropdown = true }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = selectedYearStr, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                                DropdownMenu(
                                    expanded = expandedYearDropdown,
                                    onDismissRequest = { expandedYearDropdown = false },
                                    modifier = Modifier.background(Color(15, 23, 42))
                                ) {
                                    yearsList.forEach { y ->
                                        DropdownMenuItem(
                                            text = { Text(y, color = Color.White, fontSize = 12.sp) },
                                            onClick = {
                                                selectedYearStr = y
                                                expandedYearDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mide el rendimiento del negocio a lo largo del tiempo y detecta la estacionalidad de ganancias mensuales.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary)
                            }
                        } else if (errorMessage != null) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(240.dp),
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
                            VentasLineChart(salesData = monthlySalesData)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val monthlyNames = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
                                    var textReport = "AcaciosWork - Reporte de Ganancias Mensuales ($selectedYearStr)\n" +
                                            "Fecha de generación: ${java.time.LocalDate.now()}\n" +
                                            "--------------------------------------------------\n"
                                    monthlySalesData.forEachIndexed { idx, value ->
                                        val name = monthlyNames.getOrElse(idx) { "Mes ${idx+1}" }
                                        textReport += "$name: ${com.acacioswork.util.ConfigManager.formatCurrency(value)}\n"
                                    }
                                    textReport += "--------------------------------------------------\n"
                                    textReport += "Ganancia total acumulada: ${com.acacioswork.util.ConfigManager.formatCurrency(monthlySalesData.sum())}\n"
                                    com.acacioswork.util.ReportSharing.shareReportText(context, "Reporte de Ganancias Mensuales $selectedYearStr", textReport)
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

            // 2. Gráfico de Ventas por Categoría de Producto
            item {
                CategoriasChartCard()
            }
        }
    }
}
