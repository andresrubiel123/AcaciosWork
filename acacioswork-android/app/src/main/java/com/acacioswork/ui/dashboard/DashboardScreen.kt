package com.acacioswork.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import com.acacioswork.network.SessionManager
import com.acacioswork.ui.clientes.ClientesTab
import com.acacioswork.ui.inventario.InventarioTab
import com.acacioswork.ui.proveedores.ProveedoresTab
import com.acacioswork.ui.reportes.ReportesTab
import com.acacioswork.ui.ventas.VentasTab
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.ui.inventario.InventarioViewModel
import java.text.NumberFormat
import java.util.Locale

sealed class Pantalla(val ruta: String, val titulo: String, val icon: ImageVector) {
    object Welcome : Pantalla("welcome", "Inicio", Icons.Default.Home)
    object Inventario : Pantalla("inventario", "Inventario", Icons.Default.ShoppingCart)
    object Vender : Pantalla("vender", "Vender", Icons.Default.AddCircle)
    object Reportes : Pantalla("reportes", "Reportes", Icons.Default.List)
    object Clientes : Pantalla("clientes", "Clientes", Icons.Default.Person)
    object Proveedores : Pantalla("proveedores", "Proveedores", Icons.Default.Build)
    object Configuracion : Pantalla("configuracion", "Configuración", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Welcome) }
    
    val inventarioViewModel: InventarioViewModel = viewModel()

    LaunchedEffect(pantallaActual) {
        if (com.acacioswork.util.ConfigManager.globalConfig == null) {
            com.acacioswork.util.ConfigManager.loadConfiguracion()
        }
        if (pantallaActual == Pantalla.Welcome) {
            inventarioViewModel.cargarProductos()
        }
    }
    
    val userName = SessionManager.userFullName ?: "Manuel Diaz"

    /** Transición infinita para el color verde neón del título y usuario. @author RADJ */
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = NeonGreenDim,
        targetValue = NeonGreen,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonColor"
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar Panel lateral
        Column(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
                .background(BgCard)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AcaciosWork",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = animatedColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = animatedColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = userName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = animatedColor
                    )
                }

                val pantallas = listOf(
                    Pantalla.Welcome,
                    Pantalla.Inventario,
                    Pantalla.Vender,
                    Pantalla.Reportes,
                    Pantalla.Clientes,
                    Pantalla.Proveedores,
                    Pantalla.Configuracion
                )

                pantallas.forEach { pantalla ->
                    val selected = pantallaActual == pantalla
                    TextButton(
                        onClick = { pantallaActual = pantalla },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                color = if (selected) Primary.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = pantalla.icon,
                                contentDescription = null,
                                tint = if (selected) Primary else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = pantalla.titulo,
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) TextLight else TextMuted
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                HorizontalDivider(color = TextMuted.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            SessionManager.getInstance(context).clearSession()
                            onLogout()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(AlertRed.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = AlertRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Salir", color = AlertRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
        ) {
            when (pantallaActual) {
                Pantalla.Welcome -> {
                    val totalProductos by inventarioViewModel.totalProductos.collectAsState()
                    val stockBajoCount by inventarioViewModel.stockBajoCount.collectAsState()
                    val valorTotal by inventarioViewModel.valorTotalInventario.collectAsState()
                    val productos by inventarioViewModel.productos.collectAsState()
                    var searchQuery by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        /** Encabezado de la Sección de Inicio. @author RADJ */
                        Text(
                            text = "Resumen de Inventario",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Vista rápida del estado de existencias",
                            fontSize = 12.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tarjetas de Estadísticas (Fila responsiva)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.acacioswork.ui.inventario.EstadisticaCard(
                                title = "Total Prod.",
                                value = totalProductos.toString(),
                                icon = Icons.Default.ShoppingCart,
                                iconColor = Primary,
                                modifier = Modifier.weight(1f)
                            )
                            com.acacioswork.ui.inventario.EstadisticaCard(
                                title = "Stock Bajo",
                                value = stockBajoCount.toString(),
                                icon = Icons.Default.Warning,
                                iconColor = AlertRed,
                                modifier = Modifier.weight(1f)
                            )
                            com.acacioswork.ui.inventario.EstadisticaCard(
                                title = "Valor Total",
                                value = com.acacioswork.util.ConfigManager.formatCurrency(valorTotal),
                                icon = Icons.Default.Info,
                                iconColor = AccentGreen,
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        /** Buscador de la tabla de inicio. @author RADJ */
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar producto...", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgCard,
                                containerColor = BgCard,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        /** Tabla simplificada de productos. @author RADJ */
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Encabezado de la tabla
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Código", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text("Nombre", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.8f))
                                    Text("Unidad", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text("Stock", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                    Text("Estado", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                }
                                HorizontalDivider(color = BgDark, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(8.dp))

                                val filteredProducts = productos.filter {
                                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                                            (it.codigoBarras?.contains(searchQuery) ?: false)
                                }

                                if (filteredProducts.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "No se encontraron productos.", color = TextMuted)
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredProducts) { p ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = p.codigoBarras ?: "N/A",
                                                    fontSize = 12.sp,
                                                    color = TextLight,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = p.nombre,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextLight,
                                                    modifier = Modifier.weight(1.8f)
                                                )
                                                Text(
                                                    text = p.unidadMedida ?: "Unidad",
                                                    fontSize = 12.sp,
                                                    color = TextLight,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                
                                                // Barra de stock en Compose
                                                Column(modifier = Modifier.weight(2f).padding(end = 8.dp)) {
                                                    val opt = if (p.stockOptimo > 0) p.stockOptimo else 200
                                                    val pct = Math.round((p.stockActual.toDouble() / opt) * 100).toInt()
                                                    val color = if (pct <= 30) AlertRed else if (pct <= 69) AccentOrange else AccentGreen
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("${p.stockActual}/${opt}", fontSize = 10.sp, color = TextLight)
                                                        Text("${pct}%", fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    LinearProgressIndicator(
                                                        progress = Math.min(pct / 100f, 1f),
                                                        color = color,
                                                        trackColor = BgDark,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(6.dp)
                                                            .clip(RoundedCornerShape(3.dp))
                                                    )
                                                }

                                                // Estado
                                                Box(
                                                    modifier = Modifier.weight(1f),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val activo = p.estado == 1
                                                    Text(
                                                        text = if (activo) "Activo" else "Inactivo",
                                                        color = if (activo) AccentGreen else AlertRed,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Pantalla.Inventario -> InventarioTab(viewModel = inventarioViewModel)
                Pantalla.Vender -> VentasTab()
                Pantalla.Reportes -> ReportesTab()
                Pantalla.Clientes -> ClientesTab()
                Pantalla.Proveedores -> ProveedoresTab()
                Pantalla.Configuracion -> com.acacioswork.ui.configuracion.ConfiguracionScreen()
            }
        }
    }
}
