package com.acacioswork.ui.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Configuracion
import com.acacioswork.ui.theme.*

/**
 * Pestaña Diseño del Ticket de la configuración.
 * @author RADJ / Antigravity
 */
@Composable
fun TicketTabContent(
    config: Configuracion,
    onConfigChange: (Configuracion) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Diseño del Ticket", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Primary, fontSize = 16.sp)

        OutlinedTextField(
            value = config.ticketLogotipo,
            onValueChange = { onConfigChange(config.copy(ticketLogotipo = it)) },
            label = { Text("URL o Base64 del Logotipo", color = TextMuted) },
            placeholder = { Text("https://...", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors()
        )

        OutlinedTextField(
            value = config.ticketEncabezado,
            onValueChange = { onConfigChange(config.copy(ticketEncabezado = it)) },
            label = { Text("Encabezado del Ticket", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            maxLines = 3
        )

        OutlinedTextField(
            value = config.ticketPiePagina,
            onValueChange = { onConfigChange(config.copy(ticketPiePagina = it)) },
            label = { Text("Pie de Página del Ticket", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            maxLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = config.ticketAnchoMm.toString(),
                onValueChange = { onConfigChange(config.copy(ticketAnchoMm = it.toIntOrNull() ?: 80)) },
                label = { Text("Ancho (mm)", color = TextMuted) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors()
            )
            OutlinedTextField(
                value = config.ticketAltoMm.toString(),
                onValueChange = { onConfigChange(config.copy(ticketAltoMm = it.toIntOrNull() ?: 297)) },
                label = { Text("Alto (mm)", color = TextMuted) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = config.ticketMargenIzq.toString(),
                onValueChange = { onConfigChange(config.copy(ticketMargenIzq = it.toIntOrNull() ?: 5)) },
                label = { Text("Margen Izq (mm)", color = TextMuted) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors()
            )
            OutlinedTextField(
                value = config.ticketMargenDer.toString(),
                onValueChange = { onConfigChange(config.copy(ticketMargenDer = it.toIntOrNull() ?: 5)) },
                label = { Text("Margen Der (mm)", color = TextMuted) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors()
            )
        }
    }
}
