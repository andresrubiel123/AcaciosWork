package com.acacioswork.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Producto
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.inventario.EstadisticaCard
import com.acacioswork.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pestaña de Inicio (Dashboard) de la aplicación Android.
 * Muestra el resumen de inventario, costo y ganancias, alertas y el top de stock bajo.
 * 
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeTab(
    @Suppress("UNUSED_PARAMETER") viewModel: com.acacioswork.ui.inventario.InventarioViewModel = viewModel()
) {
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Cargar productos
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val response = RetrofitClient.apiService.getProductos()
            if (response.success && response.data != null) {
                productos = response.data
            } else {
                errorMessage = response.message
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Error al conectar con el servidor."
        } finally {
            isLoading = false
        }
    }

    // 2. Calcular valor venta, valor costo y ganancia estimada
    val valorVenta = remember(productos) {
        productos.sumOf { it.stockActual * it.precioVenta }
    }
    val valorCosto = remember(productos) {
        productos.sumOf { it.stockActual * it.precioCompra }
    }
    val ganancia = remember(valorVenta, valorCosto) {
        valorVenta - valorCosto
    }

    // 3. Lógica del top 10 resumen del Dashboard (idéntico a la versión Web)
    val getPct = { p: Producto ->
        val actual = p.stockActual.toDouble()
        val optimo = if (p.stockOptimo > 0) p.stockOptimo.toDouble() else 200.0
        (actual / optimo) * 100.0
    }

    val summaryProducts = remember(productos, searchQuery) {
        if (searchQuery.isBlank()) {
            productos
                .sortedBy { getPct(it) }
                .take(5)
        } else {
            productos.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Fecha actual para la cabecera
    val dateStr = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", java.util.Locale("es", "ES")))
            .replaceFirstChar { it.uppercase() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado de Bienvenida
        item {
            Column {
                Text(
                    text = "¡Bienvenido de nuevo!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Cargando / Error
        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        } else if (errorMessage != null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "", color = AlertRed, fontSize = 14.sp)
                }
            }
        }

        // Tarjetas Estadísticas de Inicio (Valor Costo, Valor Inventario, Ganancia)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    EstadisticaCard(
                        title = "Ganancia Estimada",
                        value = com.acacioswork.util.ConfigManager.formatCurrency(ganancia),
                        icon = Icons.Default.Info,
                        iconColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    EstadisticaCard(
                        title = "Valor Costo",
                        value = com.acacioswork.util.ConfigManager.formatCurrency(valorCosto),
                        icon = Icons.Default.Info,
                        iconColor = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    EstadisticaCard(
                        title = "Valor Inventario",
                        value = com.acacioswork.util.ConfigManager.formatCurrency(valorVenta),
                        icon = Icons.Default.Info,
                        iconColor = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Buscador
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar producto...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BgCard,
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Tabla de existencias (La cabecera de la tabla)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCard, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "Resumen de Bodega (Top 5)" else "Resultados de Búsqueda",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nombre", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.8f))
                    Text("Unidad", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Stock", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                    Text("Estado", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                HorizontalDivider(color = BgDark, thickness = 1.dp)
            }
        }

        // Lista de productos filtrados / ordenados
        if (summaryProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No se encontraron productos.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(summaryProducts) { p ->
                DashboardProductCard(p)
            }
        }
    }
}
