package com.acacioswork.ui.configuracion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import com.acacioswork.model.Configuracion
import com.acacioswork.network.SessionManager
import com.acacioswork.ui.theme.*
import com.acacioswork.util.ConfigManager
import kotlinx.coroutines.launch

enum class ConfigTab {
    GENERAL, HARDWARE, TICKET
}

/**
 * Pantalla de Configuración con paridad al 100% de la versión Web.
 * Soporta navegación por pestañas, configuración de hardware POS, botón de guardar en cabecera,
 * botón de cerrar sesión y footer de copyright.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf(Configuracion()) }
    var initialized by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(ConfigTab.GENERAL) }

    LaunchedEffect(Unit) {
        if (ConfigManager.globalConfig == null) {
            ConfigManager.loadConfiguracion()
        }
        ConfigManager.globalConfig?.let {
            config = it.copy()
            initialized = true
        }
    }

    if (!initialized) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Row (Paridad Web: Título + Botón Guardar en Header)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("Configuración del Sistema", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text("Ajustes globales y hardware POS", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                ConfigManager.saveConfiguracion(config)
                                Toast.makeText(context, "💾 Configuración guardada correctamente.", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "❌ Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Guardar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Pestañas (General, Hardware, Ticket)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(text = "General", selected = currentTab == ConfigTab.GENERAL, onClick = { currentTab = ConfigTab.GENERAL }, modifier = Modifier.weight(1f))
                TabButton(text = "Hardware", selected = currentTab == ConfigTab.HARDWARE, onClick = { currentTab = ConfigTab.HARDWARE }, modifier = Modifier.weight(1f))
                TabButton(text = "Ticket", selected = currentTab == ConfigTab.TICKET, onClick = { currentTab = ConfigTab.TICKET }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Principal con contenido deslizable
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentTab) {
                        ConfigTab.GENERAL -> GeneralTabContent(config = config, onConfigChange = { config = it })
                        ConfigTab.HARDWARE -> HardwareTabContent(config = config, onConfigChange = { config = it })
                        ConfigTab.TICKET -> TicketTabContent(config = config, onConfigChange = { config = it })
                    }


                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Primary else BgCard,
            contentColor = if (selected) TextLight else TextMuted
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
