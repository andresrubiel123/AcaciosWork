package com.acacioswork.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.acacioswork.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.acacioswork.ui.preguntas_ia.PreguntasIaScreen
import com.acacioswork.ui.welcome.WelcomeTab
import com.acacioswork.ui.alertas.AlertasTab
import com.acacioswork.ui.graficos.GraficosTab
import com.acacioswork.ui.historial.HistorialTab
import com.acacioswork.ui.usuarios.UsuariosTab
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.ui.inventario.InventarioViewModel

/**
 * Pantalla principal del Dashboard que gestiona la barra de navegación lateral y el contenido central.
 * @author RADJ
 */
sealed class Pantalla(val ruta: String, val titulo: String, val icon: ImageVector) {
    object Welcome : Pantalla("welcome", "Inicio", Icons.Default.Home)
    object Inventario : Pantalla("inventario", "Inventario", Icons.Default.ShoppingCart)
    object Vender : Pantalla("vender", "Vender", Icons.Default.AddCircle)
    object Proveedores : Pantalla("proveedores", "Proveedores", Icons.Default.Build)
    object Clientes : Pantalla("clientes", "Clientes", Icons.Default.Person)
    object Reportes : Pantalla("reportes", "Reportes", Icons.AutoMirrored.Filled.List)
    object Alertas : Pantalla("alertas", "Alertas Stock", Icons.Default.Warning)
    object PreguntasIA : Pantalla("preguntas_ia", "Preguntas IA", Icons.Default.Info)
    object Graficos : Pantalla("graficos", "Gráficos", Icons.Default.Star)
    object Historial : Pantalla("historial", "Historial", Icons.Default.Refresh)
    object Usuarios : Pantalla("usuarios", "Usuarios", Icons.Default.AccountBox)
    object Configuracion : Pantalla("configuracion", "Configuración", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    val DEFAULT_USER = "Manuel Diaz"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Welcome) }
    val inventarioViewModel: InventarioViewModel = viewModel()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(pantallaActual) {
        if (com.acacioswork.util.ConfigManager.globalConfig == null) {
            com.acacioswork.util.ConfigManager.loadConfiguracion()
        }
        if (pantallaActual == Pantalla.Welcome) {
            inventarioViewModel.cargarProductos()
        }
    }
    
    val userName = SessionManager.userFullName ?: DEFAULT_USER
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BgCard,
                modifier = Modifier
                    .width(240.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "Logo AcaciosWork",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AcaciosWork",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedColor
                            )
                        }

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
                            Pantalla.Proveedores,
                            Pantalla.Clientes,
                            Pantalla.Reportes,
                            Pantalla.Alertas,
                            Pantalla.Historial,
                            Pantalla.Usuarios,
                            Pantalla.Configuracion
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            pantallas.forEach { pantalla ->
                                val selected = pantallaActual == pantalla
                                TextButton(
                                    onClick = { 
                                        pantallaActual = pantalla 
                                        coroutineScope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
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
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = TextMuted.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))
                            
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.close()
                                        SessionManager.getInstance(context).clearSession()
                                        onLogout()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .background(AlertRed.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = null,
                                        tint = AlertRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Cerrar Sesión", color = AlertRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Copyright © 2026 Rubiel Andrés Díaz",
                                color = TextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                            Text(
                                text = "Contacto: andresrubiel@gmail.com",
                                color = TextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "Logo AcaciosWork",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AcaciosWork",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedColor
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = TextLight
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                tint = TextMuted
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BgCard,
                        titleContentColor = TextLight
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BgDark)
            ) {
                when (pantallaActual) {
                    Pantalla.Welcome -> WelcomeTab(inventarioViewModel)
                    Pantalla.Inventario -> InventarioTab(viewModel = inventarioViewModel)
                    Pantalla.Vender -> VentasTab()
                    Pantalla.Proveedores -> ProveedoresTab()
                    Pantalla.Clientes -> ClientesTab()
                    Pantalla.Reportes -> ReportesTab()
                    Pantalla.Alertas -> AlertasTab(viewModel = inventarioViewModel)
                    Pantalla.PreguntasIA -> PreguntasIaScreen()
                    Pantalla.Graficos -> GraficosTab()
                    Pantalla.Historial -> HistorialTab()
                    Pantalla.Usuarios -> UsuariosTab()
                    Pantalla.Configuracion -> com.acacioswork.ui.configuracion.ConfiguracionScreen()
                }
            }
        }
    }
}
