package com.acacioswork.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Producto
import com.acacioswork.ui.theme.*

/**
 * Diálogo modal para crear o editar un producto de inventario en Android.
 * @author RADJ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormDialog(
    producto: Producto?,
    onDismiss: () -> Unit,
    onSave: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var codigoBarras by remember { mutableStateOf(producto?.codigoBarras ?: "") }
    var cantidad by remember { mutableStateOf(producto?.stockActual?.toString() ?: "") }
    var stockMinimo by remember { mutableStateOf(producto?.stockMinimo?.toString() ?: "5") }
    var stockOptimo by remember { mutableStateOf(producto?.stockOptimo?.toString() ?: "200") }
    var precioCompra by remember { mutableStateOf(producto?.precioCompra?.toString() ?: "") }
    var precioVenta by remember { mutableStateOf(producto?.precioVenta?.toString() ?: "") }
    var iva by remember { mutableStateOf(producto?.iva?.toString() ?: "19") }
    var unidadMedida by remember { mutableStateOf(producto?.unidadMedida ?: "") }
    var fechaVencimiento by remember { mutableStateOf(producto?.fechaVencimiento ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (producto != null) "Editar Producto" else "Crear Producto",
                color = TextLight,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = BgCard,
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Producto", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = unidadMedida,
                        onValueChange = { unidadMedida = it },
                        label = { Text("Unidad de Medida", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = codigoBarras,
                        onValueChange = { codigoBarras = it },
                        label = { Text("Código de Barras", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = fechaVencimiento,
                        onValueChange = { fechaVencimiento = it },
                        label = { Text("Fecha Vencimiento (AAAA-MM-DD)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Stock", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockMinimo,
                            onValueChange = { stockMinimo = it },
                            label = { Text("Mínimo", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockOptimo,
                            onValueChange = { stockOptimo = it },
                            label = { Text("Óptimo", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = precioCompra,
                            onValueChange = { precioCompra = it },
                            label = { Text("P. Compra", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = iva,
                            onValueChange = { iva = it },
                            label = { Text("IVA %", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = precioVenta,
                        onValueChange = { precioVenta = it },
                        label = { Text("Precio Venta", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = Producto(
                        id = producto?.id,
                        nombre = nombre,
                        codigoBarras = if (codigoBarras.isBlank()) null else codigoBarras,
                        stockActual = cantidad.toIntOrNull() ?: 0,
                        precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
                        precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                        iva = iva.toDoubleOrNull() ?: 19.0,
                        estado = producto?.estado ?: 1,
                        stockMinimo = stockMinimo.toIntOrNull() ?: 5,
                        stockOptimo = stockOptimo.toIntOrNull() ?: 200,
                        unidadMedida = if (unidadMedida.isBlank()) "Unidad" else unidadMedida,
                        fechaVencimiento = if (fechaVencimiento.isBlank()) null else fechaVencimiento
                    )
                    onSave(p)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF97316), Color(0xFFEF4444))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text("Guardar", color = TextLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        }
    )
}
