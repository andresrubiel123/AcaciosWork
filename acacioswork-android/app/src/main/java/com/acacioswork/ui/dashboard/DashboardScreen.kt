package com.acacioswork.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                    Pantalla.Proveedores
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
                    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = BgCard)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Bienvenido a AcaciosWork",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Por favor, seleccione una opción en el menú lateral para comenzar.",
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjetas de Estadísticas (Fila responsiva)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                value = formatCurrency.format(valorTotal),
                                icon = Icons.Default.Info,
                                iconColor = AccentGreen,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                    }
                }
                Pantalla.Inventario -> InventarioTab(viewModel = inventarioViewModel)
                Pantalla.Vender -> VentasTab()
                Pantalla.Reportes -> ReportesTab()
                Pantalla.Clientes -> ClientesTab()
                Pantalla.Proveedores -> ProveedoresTab()
            }
        }
    }
}
