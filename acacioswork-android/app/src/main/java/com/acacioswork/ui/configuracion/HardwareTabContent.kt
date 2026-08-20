package com.acacioswork.ui.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Configuracion
import com.acacioswork.ui.theme.*

/**
 * Pestaña Hardware de la configuración (paridad web).
 * @author RADJ / Antigravity
 */
@Composable
fun HardwareTabContent(
    config: Configuracion,
    onConfigChange: (Configuracion) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Configuración de Hardware POS", fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp)

        // 1. Lector de Código de Barras
        HardwareBox(title = "🏷️ Lector de Código de Barras") {
            val modes = listOf("KEYBOARD" to "Emulación de Teclado (USB HID)", "SERIAL" to "Puerto Serial / COM Virtual")
            DropdownField(
                label = "Modo de Conexión",
                options = modes,
                selectedKey = config.barcodeMode,
                onSelected = { onConfigChange(config.copy(barcodeMode = it)) }
            )
            if (config.barcodeMode == "SERIAL") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = config.barcodePort,
                    onValueChange = { onConfigChange(config.copy(barcodePort = it)) },
                    label = { Text("Puerto Serial COM", color = TextMuted) },
                    placeholder = { Text("Ej. COM3", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        }

        // 2. Báscula / Balanza
        HardwareBox(title = "⚖️ Báscula / Balanza") {
            val scaleEnabledOptions = listOf("false" to "Desactivada (No pesar automáticamente)", "true" to "Habilitada")
            DropdownField(
                label = "Estado de la Báscula",
                options = scaleEnabledOptions,
                selectedKey = config.scaleEnabled.toString(),
                onSelected = { onConfigChange(config.copy(scaleEnabled = it.toBoolean())) }
            )
            if (config.scaleEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                val protocols = listOf(
                    "CAS" to "CAS ER-Plus / AP-1",
                    "TOLEDO" to "Toledo / Systel",
                    "DIBAL" to "Dibal / Kretz",
                    "CONTINUOUS" to "Transmisión Continua Genérica",
                    "BARCODE" to "Código de Barras Embebido en Ticket"
                )
                DropdownField(
                    label = "Protocolo de Comunicación",
                    options = protocols,
                    selectedKey = config.scaleProtocol,
                    onSelected = { onConfigChange(config.copy(scaleProtocol = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = config.scalePort,
                    onValueChange = { onConfigChange(config.copy(scalePort = it)) },
                    label = { Text("Puerto COM de la Báscula", color = TextMuted) },
                    placeholder = { Text("Ej. COM4", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                val baudrates = listOf("9600" to "9600 bps (Estándar)", "4800" to "4800 bps", "19200" to "19200 bps")
                DropdownField(
                    label = "Velocidad de Puerto (Baudios)",
                    options = baudrates,
                    selectedKey = config.scaleBaudrate.toString(),
                    onSelected = { onConfigChange(config.copy(scaleBaudrate = it.toIntOrNull() ?: 9600)) }
                )
            }
        }

        // 3. Impresora Térmica
        HardwareBox(title = "🖨️ Impresora Térmica") {
            OutlinedTextField(
                value = config.impresoraActiva,
                onValueChange = { onConfigChange(config.copy(impresoraActiva = it)) },
                label = { Text("Impresora en Windows (Cola del Driver)", color = TextMuted) },
                placeholder = { Text("Ej. EPSON TM-T20II", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )
            Spacer(modifier = Modifier.height(10.dp))
            val interfaces = listOf(
                "SYSTEM" to "Diálogo del Sistema (HTML / Impresora de Windows)",
                "ESC_POS_RAW" to "Directo ESC/POS (Puerto Local / Driver Genérico)",
                "NETWORK" to "Directo ESC/POS sobre Red (TCP/IP)"
            )
            DropdownField(
                label = "Interfaz de Conectividad",
                options = interfaces,
                selectedKey = config.printerInterface,
                onSelected = { onConfigChange(config.copy(printerInterface = it)) }
            )
            if (config.printerInterface != "SYSTEM") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = config.printerPort,
                    onValueChange = { onConfigChange(config.copy(printerPort = it)) },
                    label = { Text("Puerto o Dirección IP", color = TextMuted) },
                    placeholder = { Text("Ej. 192.168.1.100 o LPT1", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        }

        // 4. Cajón Monedero
        HardwareBox(title = "💵 Cajón Monedero") {
            val cajonOptions = listOf(
                "true" to "Conectado a la Impresora de Tickets (Puerto RJ11/RJ12)",
                "false" to "Conectado Directo por Puerto Serial/COM"
            )
            DropdownField(
                label = "Modo de Conexión del Cajón",
                options = cajonOptions,
                selectedKey = config.cajonConectadoImpresora.toString(),
                onSelected = { onConfigChange(config.copy(cajonConectadoImpresora = it.toBoolean())) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = config.cajonComando,
                onValueChange = { onConfigChange(config.copy(cajonComando = it)) },
                label = { Text("Secuencia de Apertura ESC/POS (Decimal)", color = TextMuted) },
                placeholder = { Text("Ej. 27,112,0,25,250", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )
        }

        // 5. Datáfono POS-Link
        HardwareBox(title = "💳 Datáfono Integrado (POS-Link)") {
            val cardOptions = listOf("false" to "Desactivada (Registro manual en pantalla)", "true" to "Habilitada (POS-Link activo)")
            DropdownField(
                label = "Integración con Datáfono",
                options = cardOptions,
                selectedKey = config.datafonoIntegracion.toString(),
                onSelected = { onConfigChange(config.copy(datafonoIntegracion = it.toBoolean())) }
            )
            if (config.datafonoIntegracion) {
                Spacer(modifier = Modifier.height(10.dp))
                val providers = listOf(
                    "REDEBAN" to "Redeban (Colombia)",
                    "CREDIBANCO" to "Credibanco (Colombia)",
                    "GENERIC" to "Genérico TCP/IP POS-Link"
                )
                DropdownField(
                    label = "Proveedor / Protocolo",
                    options = providers,
                    selectedKey = config.datafonoProveedor,
                    onSelected = { onConfigChange(config.copy(datafonoProveedor = it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = config.datafonoPuerto,
                    onValueChange = { onConfigChange(config.copy(datafonoPuerto = it)) },
                    label = { Text("Puerto COM / IP del Datáfono", color = TextMuted) },
                    placeholder = { Text("Ej. COM5 o 192.168.1.150", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = config.datafonoTerminalId,
                    onValueChange = { onConfigChange(config.copy(datafonoTerminalId = it)) },
                    label = { Text("ID del Terminal (Terminal ID)", color = TextMuted) },
                    placeholder = { Text("Ej. TML00912", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        }
    }
}

@Composable
private fun HardwareBox(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgDark.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = Primary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    options: List<Pair<String, String>>,
    selectedKey: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = options.firstOrNull { it.first == selectedKey }?.second ?: selectedKey

    Column {
        Text(text = label, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BgDark)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedText, color = TextLight, fontSize = 13.sp)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextMuted)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(BgCard)
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(text = opt.second, color = TextLight, fontSize = 13.sp) },
                        onClick = {
                            onSelected(opt.first)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
