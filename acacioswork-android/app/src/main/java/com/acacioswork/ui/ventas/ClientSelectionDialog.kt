package com.acacioswork.ui.ventas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.acacioswork.model.Cliente
import com.acacioswork.ui.theme.*

/**
 * Diálogo modular para seleccionar un cliente registrado en la venta.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSelectionDialog(
    clientes: List<Cliente>,
    onDismiss: () -> Unit,
    onSelect: (Cliente?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredClientes = clientes.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.numeroDocumento.contains(searchQuery)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Seleccionar Cliente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cliente...", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        focusedContainerColor = BgDark,
                        unfocusedContainerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(null) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("— Venta sin cliente registrado —", color = TextMuted, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = BgDark, thickness = 1.dp)
                    }

                    items(filteredClientes) { cliente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(cliente) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(cliente.nombre, color = TextLight, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("Doc: ${cliente.numeroDocumento}", color = TextMuted, fontSize = 12.sp)
                            }
                            if (cliente.frecuente) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Frecuente", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextMuted)
                    }
                }
            }
        }
    }
}
