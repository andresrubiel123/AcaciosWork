package com.acacioswork.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Producto
import com.acacioswork.ui.theme.*

/**
 * Tarjeta de fila para renderizar productos en la lista del dashboard de Inicio.
 * @author RADJ / Antigravity
 */
@Composable
fun DashboardProductCard(p: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (p.stockActual <= p.stockMinimo) AlertRed else AccentGreen, shape = androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = p.nombre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextLight,
                modifier = Modifier.weight(1.8f)
            )
            Text(
                text = p.unidadMedida,
                fontSize = 12.sp,
                color = TextLight,
                modifier = Modifier.weight(1f)
            )
            // Barra de stock
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
                    progress = { Math.min(pct / 100f, 1f) },
                    color = color,
                    trackColor = BgDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
            Box(
                modifier = Modifier.weight(1.2f),
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
