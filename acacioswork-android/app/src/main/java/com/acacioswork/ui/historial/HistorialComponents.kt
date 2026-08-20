package com.acacioswork.ui.historial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Cliente
import com.acacioswork.model.Venta
import com.acacioswork.ui.theme.*
import com.acacioswork.util.ConfigManager

/**
 * Tarjeta de estadística para el encabezado del Historial de Ventas.
 * @author RADJ / Antigravity
 */
@Composable
fun HistorialStatCard(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

/**
 * Fila individual de venta en el historial, expandible para mostrar el detalle de productos.
 * Paridad 100% con la versión web: badge de productos en indigo, total en verde.
 * @author RADJ / Antigravity
 */
@Composable
fun HistorialVentaRow(
    venta: Venta,
    clientMap: Map<Long?, Cliente>,
    isLast: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val clienteNombre = clientMap[venta.idCliente]?.nombre
        ?: if (venta.idCliente != null) "Cliente #${venta.idCliente}" else "Sin cliente"

    val totalVenta = if (venta.valorTotal > 0.0) venta.valorTotal
    else venta.detalles.sumOf { it.cantidad * it.precioUnitario }

    val fechaFormateada = venta.fechaHora
        ?.replace("T", " ")
        ?.let { if (it.length > 16) it.substring(0, 16) else it }
        ?: "—"

    val nProductos = venta.detalles.size

    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = if (isLast) RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        else RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            // Fila principal (paridad web tr)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ID (paridad web: monospace muted)
                Text(
                    text = "#${venta.id}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp)
                )

                // Fecha
                Column(modifier = Modifier.weight(1.4f)) {
                    Text(
                        text = fechaFormateada,
                        fontSize = 12.sp,
                        color = TextLight,
                        maxLines = 2
                    )
                }

                // Cliente
                Text(
                    text = clienteNombre,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (venta.idCliente != null) TextLight else TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1.5f)
                )

                // Total
                Text(
                    text = ConfigManager.formatCurrency(totalVenta),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.weight(1f)
                )

                // Flecha expandir
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Badge de productos (paridad web: badge indigo/purple)
            Row(
                modifier = Modifier.padding(start = 54.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "📦 $nProductos producto${if (nProductos != 1) "s" else ""}",
                        fontSize = 11.sp,
                        color = Color(0xFFA5B4FC),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Panel expandible de detalles (paridad web: expandable row)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Detalle de Productos",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )

                    if (venta.detalles.isEmpty()) {
                        Text("Sin detalles disponibles.", fontSize = 12.sp, color = TextMuted)
                    } else {
                        venta.detalles.forEach { detalle ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${detalle.cantidad}× Producto #${detalle.idProducto}",
                                    fontSize = 12.sp,
                                    color = TextLight,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = ConfigManager.formatCurrency(detalle.precioUnitario * detalle.cantidad),
                                    fontSize = 12.sp,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.06f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Total: ${ConfigManager.formatCurrency(totalVenta)}",
                                fontSize = 13.sp,
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Separador entre filas
            if (!isLast) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    }
}
