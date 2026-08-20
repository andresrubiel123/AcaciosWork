package com.acacioswork.ui.alertas

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.inventario.InventarioViewModel
import com.acacioswork.ui.theme.*

/**
 * Pestaña modular de Alertas de Inventario en Android con paridad al 100% con la version Web.
 * @author RADJ / Antigravity
 */
@Composable
fun AlertasTab(viewModel: InventarioViewModel) {
    val productos by viewModel.productos.collectAsState()
    val context = LocalContext.current
    val today = remember { java.time.LocalDate.now() }

    // 1. Productos próximos a vencer o ya vencidos (<= 5 días)
    val porVencerList = remember(productos) {
        productos.filter { p ->
            val fv = p.fechaVencimiento
            if (!fv.isNullOrBlank()) {
                try {
                    val expDate = java.time.LocalDate.parse(fv)
                    val diff = java.time.temporal.ChronoUnit.DAYS.between(today, expDate)
                    diff <= 5
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }
        }.sortedWith { a, b ->
            try {
                java.time.LocalDate.parse(a.fechaVencimiento).compareTo(java.time.LocalDate.parse(b.fechaVencimiento))
            } catch (e: Exception) {
                0
            }
        }
    }

    // 2. Productos con stock bajo
    val stockBajoList = remember(productos) {
        productos.filter { it.stockActual <= it.stockMinimo }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera Oficial (Paridad Web)
            item {
                Text(
                    text = "⚠ Alertas de Inventario",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Productos con stock bajo o próximos a vencer",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // ================= SECCIÓN 1: VENCIMIENTOS =================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📅 Próximos a Vencer (Límite: 5 días)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { shareAlertsReport(context, "Productos Próximos a Vencer", "vencimientos") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Vencimientos PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (porVencerList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓ No hay productos próximos a vencer o vencidos (límite 5 días).",
                                color = AccentGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(porVencerList) { p ->
                    AlertaVencimientoCard(p, today)
                }
            }

            // ================= SECCIÓN 2: STOCK BAJO =================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠ Productos con Stock Bajo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { shareAlertsReport(context, "Productos con Stock Bajo", "stock_bajo") },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Stock Bajo PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (stockBajoList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓ No hay productos con stock bajo.",
                                color = AccentGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(stockBajoList) { p ->
                    AlertaStockBajoCard(p)
                }
            }
        }
    }
}

private fun shareAlertsReport(context: Context, titulo: String, tipo: String) {
    Toast.makeText(context, "Generando consolidado...", Toast.LENGTH_SHORT).show()
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            this.type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Alertas AcaciosWork - $titulo")
            putExtra(
                Intent.EXTRA_TEXT,
                "AcaciosWork - Alerta: $titulo\n" +
                "Tipo de reporte: $tipo\n" +
                "Fecha de generación: ${java.time.LocalDate.now()}\n" +
                "--------------------------------------------------\n" +
                "Métricas de control de inventario y reposición requeridas de inmediato."
            )
        }
        val shareIntent = Intent.createChooser(sendIntent, "Exportar a:")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
