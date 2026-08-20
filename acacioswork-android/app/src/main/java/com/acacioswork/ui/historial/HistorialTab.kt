package com.acacioswork.ui.historial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Cliente
import com.acacioswork.model.Venta
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import com.acacioswork.util.ConfigManager

/**
 * Pestaña de Historial de Ventas en Android con paridad al 100% con la versión Web.
 * Tabla con estadísticas, buscador, ordenado descendente y filas expandibles.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTab() {
    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val resVentas = RetrofitClient.apiService.getVentas()
            val resClientes = RetrofitClient.apiService.getClientes()
            if (resVentas.success) ventas = resVentas.data ?: emptyList()
            if (resClientes.success) clientes = resClientes.data ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val clientMap = remember(clientes) { clientes.associateBy { it.id } }

    // Ordenadas por fecha descendente (paridad web ventas.sort by fechaHora DESC)
    val sortedVentas = remember(ventas) {
        ventas.sortedByDescending { it.fechaHora ?: "" }
    }

    val totalRecaudado = remember(sortedVentas) {
        sortedVentas.sumOf { v ->
            if (v.valorTotal > 0.0) v.valorTotal
            else v.detalles.sumOf { d -> d.cantidad * d.precioUnitario }
        }
    }

    val filteredVentas = remember(sortedVentas, clientMap, searchQuery) {
        if (searchQuery.isBlank()) sortedVentas
        else sortedVentas.filter { v ->
            val cName = clientMap[v.idCliente]?.nombre ?: "Sin cliente"
            val idStr = v.id?.toString() ?: ""
            val fecha = v.fechaHora?.replace("T", " ") ?: ""
            cName.contains(searchQuery, ignoreCase = true) ||
                    idStr.contains(searchQuery) ||
                    fecha.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera (Paridad Web: 📋 Historial de Ventas)
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
                        Icon(Icons.Default.History, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("📋 Historial de Ventas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("Ventas registradas en el sistema", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }

            // Tarjetas de estadísticas (paridad web stats-row)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistorialStatCard(
                        label = "Total Ventas",
                        value = if (isLoading) "—" else "${ventas.size}",
                        valueColor = TextLight,
                        modifier = Modifier.weight(1f)
                    )
                    HistorialStatCard(
                        label = "Total Recaudado",
                        value = if (isLoading) "—" else ConfigManager.formatCurrency(totalRecaudado),
                        valueColor = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Buscador
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Buscar venta, cliente o fecha...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgCard,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contenido principal
            when {
                isLoading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(color = Primary)
                            Text("Cargando historial...", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }

                filteredVentas.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📭", fontSize = 40.sp)
                            Text(
                                if (searchQuery.isBlank()) "Sin ventas registradas."
                                else "No se encontraron resultados para \"$searchQuery\"",
                                color = TextMuted, fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {
                    // Encabezado de tabla (paridad web thead)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                                Text("FECHA", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.4f))
                                Text("CLIENTE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                Text("TOTAL", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.End, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Filas de ventas (expandibles — paridad web)
                    itemsIndexed(filteredVentas) { index, venta ->
                        HistorialVentaRow(
                            venta = venta,
                            clientMap = clientMap,
                            isLast = index == filteredVentas.lastIndex
                        )
                    }
                }
            }
        }
    }
}
