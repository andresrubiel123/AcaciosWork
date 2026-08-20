package com.acacioswork.ui.proveedores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Proveedor
import com.acacioswork.ui.theme.*

/**
 * Tarjeta modular para renderizar los detalles de un Proveedor.
 * @author RADJ / Antigravity
 */
@Composable
fun ProveedorCard(
    proveedor: Proveedor,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AccentGreen)
                    }
                    Column {
                        Text(
                            text = proveedor.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Doc: ${proveedor.numeroDocumento}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BgDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!proveedor.telefono.isNullOrBlank()) {
                    Text(text = "Teléfono: ${proveedor.telefono}", fontSize = 13.sp, color = TextLight)
                }
                if (!proveedor.email.isNullOrBlank()) {
                    Text(text = "Email: ${proveedor.email}", fontSize = 13.sp, color = TextLight)
                }
                if (!proveedor.direccion.isNullOrBlank()) {
                    Text(text = "Dirección: ${proveedor.direccion}", fontSize = 13.sp, color = TextLight)
                }
                if (!proveedor.cuentaBancaria.isNullOrBlank()) {
                    Text(text = "Cuenta: ${proveedor.cuentaBancaria}", fontSize = 13.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextLight)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AlertRed)
                }
            }
        }
    }
}
