package com.acacioswork.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Producto
import com.acacioswork.ui.theme.*

/**
 * Tarjeta de estadísticas rápidas para la cabecera.
 * @author RADJ
 */
@Composable
fun EstadisticaCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1)
            Text(text = title, fontSize = 11.sp, color = TextMuted, maxLines = 1)
        }
    }
}

/**
 * Tarjeta de producto que incluye detalles, fecha de vencimiento y botones de Entrada/Salida rápidos.
 * @author RADJ
 */
@Composable
fun ProductoCard(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEntrada: () -> Unit,
    onSalida: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = producto.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = "Cód: ${producto.codigoBarras ?: "Sin código"} | ${producto.unidadMedida}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                val opt = if (producto.stockOptimo > 0) producto.stockOptimo else 200
                val pct = Math.round((producto.stockActual.toDouble() / opt) * 100).toInt()
                val textColor = if (pct <= 30) AlertRed else if (pct <= 69) AccentOrange else AccentGreen

                Text(
                    text = "${producto.stockActual}",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BgDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Precios de compra y venta
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "P. Compra", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = com.acacioswork.util.ConfigManager.formatCurrency(producto.precioCompra),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight
                    )
                }
                Column {
                    Text(text = "IVA", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${producto.iva}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight
                    )
                }
                Column {
                    Text(text = "P. Venta", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = com.acacioswork.util.ConfigManager.formatCurrency(producto.precioVenta),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BgDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Fila de Vencimiento y Botones de Entrada/Salida
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Vencimiento", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = producto.fechaVencimiento ?: "N/A",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (producto.fechaVencimiento != null) AccentOrange else TextMuted
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onEntrada,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Entrada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button(
                        onClick = onSalida,
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Salida", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Editar y Eliminar
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextLight, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AlertRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
