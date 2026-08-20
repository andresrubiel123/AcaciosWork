package com.acacioswork.ui.preguntas_ia

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.*
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Data class para representar cada tarjeta de pregunta inteligente.
 * @author RADJ / Antigravity
 */
data class IQCardData(
    val id: String,
    val badge: String,
    val icon: String,
    val question: String,
    val alwaysActive: Boolean
)

/**
 * Pantalla de Preguntas Inteligentes con paridad al 100% con la versión Web.
 * Usa grid de 2 columnas y selección de fecha nativa con Material3.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntasIaScreen(
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Filtros de fecha (Paridad Web)
    val defaultFrom = remember { LocalDate.now().withDayOfMonth(1).toString() }
    val defaultTo = remember { LocalDate.now().toString() }
    var dateFrom by remember { mutableStateOf(defaultFrom) }
    var dateTo by remember { mutableStateOf(defaultTo) }

    // Picker dialogs
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    // Caché de base de datos
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var proveedores by remember { mutableStateOf<List<Proveedor>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }

    // Estado de respuestas e indicadores locales por tarjeta
    val answersMap = remember { mutableStateMapOf<String, String>() }
    val loadingAnswers = remember { mutableStateMapOf<String, Boolean>() }

    val isFilterActive = dateFrom.isNotBlank() && dateTo.isNotBlank()

    // Carga de base de datos inicial
    LaunchedEffect(Unit) {
        try {
            val prodRes = RetrofitClient.apiService.getProductos()
            val ventRes = RetrofitClient.apiService.getVentas()
            val cliRes = RetrofitClient.apiService.getClientes()
            val provRes = RetrofitClient.apiService.getProveedores()

            if (prodRes.success) productos = prodRes.data ?: emptyList()
            if (ventRes.success) ventas = ventRes.data ?: emptyList()
            if (cliRes.success) clientes = cliRes.data ?: emptyList()
            if (provRes.success) proveedores = provRes.data ?: emptyList()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isLoadingData = false
        }
    }

    // DatePicker — Desde
    if (showFromPicker) {
        IQDatePickerDialog(
            initialDate = dateFrom,
            onDateSelected = { date ->
                dateFrom = date
                answersMap.clear()
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }

    // DatePicker — Hasta
    if (showToPicker) {
        IQDatePickerDialog(
            initialDate = dateTo,
            onDateSelected = { date ->
                dateTo = date
                answersMap.clear()
                showToPicker = false
            },
            onDismiss = { showToPicker = false }
        )
    }

    // Definición de las 9 tarjetas IQ (paridad web)
    val questions = listOf(
        IQCardData("rentables", "Rentabilidad", "💰", "¿Cuáles fueron los productos más rentables?", false),
        IQCardData("baja-rotacion", "Rotación", "🐌", "¿Qué productos tienen baja rotación?", false),
        IQCardData("reabastecer", "Stock", "📦", "¿Qué productos debo reabastecer?", false),
        IQCardData("proveedor-caro", "Proveedores", "🏭", "¿Cuál proveedor vende más caro?", false),
        IQCardData("top-clientes", "Clientes", "👥", "¿Qué clientes compran más?", false),
        IQCardData("mejor-mes", "Ganancias", "📈", "¿Qué mes tuvo mayores ganancias?", false),
        IQCardData("perdidas", "Pérdidas", "📉", "¿Qué producto me está generando pérdidas?", false),
        IQCardData("sin-vender", "Estancados", "🕸️", "¿Qué productos llevan más tiempo sin venderse?", false),
        IQCardData("proximos-vencer", "Vencimiento", "📅", "¿Cuáles son los productos más próximos a vencerse?", true)
    )

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
            // Cabecera (Paridad Web)
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("Preguntas Inteligentes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("Análisis automático de tu negocio — selecciona un mes y haz clic en cada pregunta", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }

            // Cargando datos iniciales
            if (isLoadingData) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(color = Primary)
                            Text("Cargando datos del negocio...", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // Panel de filtro de fechas
                item {
                    IQFilterCard(
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        isFilterActive = isFilterActive,
                        onFromClick = { showFromPicker = true },
                        onToClick = { showToPicker = true }
                    )
                }

                // Grid 2×N de tarjetas IQ (paridad web)
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 2200.dp), // bounded dentro del LazyColumn
                        userScrollEnabled = false
                    ) {
                        items(questions) { q ->
                            val isEnabled = q.alwaysActive || isFilterActive
                            val answer = answersMap[q.id]
                            val isLoading = loadingAnswers[q.id] ?: false

                            IQCard(
                                q = q,
                                isEnabled = isEnabled,
                                answer = answer,
                                isLoading = isLoading,
                                onClick = {
                                    if (!isEnabled) {
                                        Toast.makeText(context, "Configura el rango de fechas primero", Toast.LENGTH_SHORT).show()
                                    } else {
                                        coroutineScope.launch {
                                            loadingAnswers[q.id] = true
                                            answersMap.remove(q.id)
                                            val filteredVentas = IntelligenceEngineHelper.filtrarVentasPorRango(ventas, dateFrom, dateTo)
                                            val ans = when (q.id) {
                                                "rentables" -> IntelligenceEngine.iqAnalizarRentables(productos, filteredVentas)
                                                "baja-rotacion" -> IntelligenceEngine.iqAnalizarBajaRotacion(productos, filteredVentas)
                                                "reabastecer" -> IntelligenceEngine.iqAnalizarReabastecer(productos)
                                                "proveedor-caro" -> IntelligenceEngine.iqAnalizarProveedorCaro(productos, proveedores)
                                                "top-clientes" -> IntelligenceEngine.iqAnalizarTopClientes(clientes, filteredVentas)
                                                "mejor-mes" -> IntelligenceEngineHelper.iqAnalizarMejorMes(productos, ventas)
                                                "perdidas" -> IntelligenceEngineHelper.iqAnalizarPerdidas(productos)
                                                "sin-vender" -> IntelligenceEngineHelper.iqAnalizarSinVender(productos, filteredVentas)
                                                "proximos-vencer" -> IntelligenceEngineHelper.iqAnalizarProximosVencer(productos)
                                                else -> "Pregunta no reconocida."
                                            }
                                            answersMap[q.id] = ans
                                            loadingAnswers[q.id] = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
