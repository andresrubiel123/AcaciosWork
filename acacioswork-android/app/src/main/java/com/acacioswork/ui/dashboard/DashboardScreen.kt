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
import com.acacioswork.network.SessionManager
import com.acacioswork.ui.clientes.ClientesTab
import com.acacioswork.ui.inventario.InventarioTab
import com.acacioswork.ui.proveedores.ProveedoresTab
import com.acacioswork.ui.reportes.ReportesTab
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch

sealed class Pantalla(val ruta: String, val titulo: String, val icon: ImageVector) {
    object Inventario : Pantalla("inventario", "Inventario", Icons.Default.ShoppingCart)
    object Reportes : Pantalla("reportes", "Reportes", Icons.Default.List)
    object Clientes : Pantalla("clientes", "Clientes", Icons.Default.Person)
    object Proveedores : Pantalla("proveedores", "Proveedores", Icons.Default.Build)
}

@OptIn(Material3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Inventario) }
    
    val userName = SessionManager.userFullName ?: "Manuel Diaz"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AcaciosWork",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = userName,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                SessionManager.getInstance(context).clearSession()
                                onLogout()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = AlertRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgCard,
                    titleContentColor = TextLight
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BgCard,
                tonalElevation = 8.dp
            ) {
                val pantallas = listOf(
                    Pantalla.Inventario,
                    Pantalla.Reportes,
                    Pantalla.Clientes,
                    Pantalla.Proveedores
                )

                pantallas.forEach { pantalla ->
                    NavigationBarItem(
                        selected = pantallaActual == pantalla,
                        onClick = { pantallaActual = pantalla },
                        icon = { Icon(pantalla.icon, contentDescription = pantalla.titulo) },
                        label = { Text(pantalla.titulo, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextLight,
                            selectedTextColor = TextLight,
                            indicatorColor = Primary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDark)
        ) {
            when (pantallaActual) {
                Pantalla.Inventario -> InventarioTab()
                Pantalla.Reportes -> ReportesTab()
                Pantalla.Clientes -> ClientesTab()
                Pantalla.Proveedores -> ProveedoresTab()
            }
        }
    }
}
