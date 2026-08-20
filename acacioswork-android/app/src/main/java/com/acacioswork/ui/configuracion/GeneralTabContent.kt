package com.acacioswork.ui.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Configuracion
import com.acacioswork.ui.theme.*

/**
 * Pestaña General de la configuración.
 * @author RADJ / Antigravity
 */
@Composable
fun GeneralTabContent(
    config: Configuracion,
    onConfigChange: (Configuracion) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Información General", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Primary, fontSize = 16.sp)

        OutlinedTextField(
            value = config.nombreEmpresa,
            onValueChange = { onConfigChange(config.copy(nombreEmpresa = it)) },
            label = { Text("Nombre de la Empresa", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors()
        )

        OutlinedTextField(
            value = config.idioma,
            onValueChange = { onConfigChange(config.copy(idioma = it)) },
            label = { Text("Idioma", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors()
        )

        OutlinedTextField(
            value = config.moneda,
            onValueChange = { onConfigChange(config.copy(moneda = it)) },
            label = { Text("Moneda (Ej: COP, USD, EUR)", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors()
        )
    }
}
