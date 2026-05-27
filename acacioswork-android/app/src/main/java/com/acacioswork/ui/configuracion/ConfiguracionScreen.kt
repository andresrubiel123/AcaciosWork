package com.acacioswork.ui.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Configuracion
import com.acacioswork.ui.theme.*
import com.acacioswork.util.ConfigManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen() {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var config by remember { mutableStateOf(Configuracion()) }
    var initialized by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp)
    ) {
        Text(
            text = "Configuración del Sistema",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Ajustes globales de la empresa, hardware y formatos",
            fontSize = 12.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Generales
                Text("Ajustes Generales", fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp)
                OutlinedTextField(
                    value = config.nombreEmpresa,
                    onValueChange = { config = config.copy(nombreEmpresa = it) },
                    label = { Text("Nombre de la Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = config.idioma,
                    onValueChange = { config = config.copy(idioma = it) },
                    label = { Text("Idioma") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = config.moneda,
                    onValueChange = { config = config.copy(moneda = it) },
                    label = { Text("Moneda (Ej: COP, USD, EUR)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                
                Divider(color = BgDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                
                // Hardware
                Text("Dispositivos Hardware", fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp)
                OutlinedTextField(
                    value = config.lectorCodigoBarras,
                    onValueChange = { config = config.copy(lectorCodigoBarras = it) },
                    label = { Text("Lector de Código de Barras") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = config.impresoraActiva,
                    onValueChange = { config = config.copy(impresoraActiva = it) },
                    label = { Text("Impresora Activa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Divider(color = BgDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                // Ticket
                Text("Formato de Ticket", fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp)
                OutlinedTextField(
                    value = config.ticketEncabezado,
                    onValueChange = { config = config.copy(ticketEncabezado = it) },
                    label = { Text("Encabezado del Ticket") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = config.ticketPiePagina,
                    onValueChange = { config = config.copy(ticketPiePagina = it) },
                    label = { Text("Pie de Página del Ticket") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    maxLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = config.ticketAnchoMm.toString(),
                        onValueChange = { config = config.copy(ticketAnchoMm = it.toIntOrNull() ?: 80) },
                        label = { Text("Ancho (mm)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors()
                    )
                    OutlinedTextField(
                        value = config.ticketAltoMm.toString(),
                        onValueChange = { config = config.copy(ticketAltoMm = it.toIntOrNull() ?: 297) },
                        label = { Text("Alto (mm)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors()
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                ConfigManager.saveConfiguracion(config)
                                showSuccessDialog = true
                            } catch (e: Exception) {
                                showErrorDialog = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Guardar Configuración", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Éxito") },
            text = { Text("Configuración guardada correctamente. Algunos cambios pueden requerir reiniciar las vistas.") },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK", color = Primary)
                }
            },
            containerColor = BgCard,
            titleContentColor = TextLight,
            textContentColor = TextMuted
        )
    }

    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Error") },
            text = { Text(showErrorDialog!!) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = null }) {
                    Text("OK", color = AlertRed)
                }
            },
            containerColor = BgCard,
            titleContentColor = TextLight,
            textContentColor = TextMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = BgDark,
    containerColor = BgDark,
    focusedTextColor = TextLight,
    unfocusedTextColor = TextLight
)
