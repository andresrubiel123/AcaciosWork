package com.acacioswork.ui.ventas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Componentes visuales modulares para la ventana de Ventas POS en Android.
 * @author RADJ / Antigravity
 */
@Composable
fun VentaResumenCard(
    subtotal: Double,
    isLoading: Boolean,
    onClear: () -> Unit,
    onRegister: () -> Unit,
    enabled: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", color = TextMuted)
                Text(com.acacioswork.util.ConfigManager.formatCurrency(subtotal), color = TextLight, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", color = TextLight, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = com.acacioswork.util.ConfigManager.formatCurrency(subtotal),
                    color = AccentGreen,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1.0f)
                ) {
                    Text("Vaciar")
                }
                Button(
                    onClick = onRegister,
                    enabled = enabled && !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    modifier = Modifier.weight(1.2f)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TextLight, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Registrar Venta", color = TextLight, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}
