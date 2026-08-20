package com.acacioswork.ui.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.acacioswork.ui.theme.*

/**
 * Componentes de renderizado visual para alertas de inventario.
 * @author RADJ / Antigravity
 */
@Composable
fun AlertaVencimientoCard(
    producto: com.acacioswork.model.Producto,
    today: java.time.LocalDate
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vence: ${producto.fechaVencimiento}",
                    fontSize = 11.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Medium
                )
            }

            val fvStr = producto.fechaVencimiento ?: ""
            val (badgeText, badgeColor) = try {
                val expDate = java.time.LocalDate.parse(fvStr)
                val diffDays = java.time.temporal.ChronoUnit.DAYS.between(today, expDate)
                if (diffDays < 0) {
                    Pair("Vencido hace ${Math.abs(diffDays)} días", AlertRed)
                } else if (diffDays == 0L) {
                    Pair("Vence HOY", AlertRed)
                } else if (diffDays == 1L) {
                    Pair("Vence Mañana", AccentOrange)
                } else {
                    Pair("Vence en ${diffDays} días", AccentOrange)
                }
            } catch (e: Exception) {
                Pair("Vencimiento N/A", AccentOrange)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = badgeText, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlertaStockBajoCard(
    producto: com.acacioswork.model.Producto
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Código: ${producto.codigoBarras ?: "N/A"}", fontSize = 11.sp, color = TextMuted)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Stock Actual", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${producto.stockActual} uds",
                        color = if (producto.stockActual == 0) AlertRed else AccentOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Mínimo", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${producto.stockMinimo} uds",
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
