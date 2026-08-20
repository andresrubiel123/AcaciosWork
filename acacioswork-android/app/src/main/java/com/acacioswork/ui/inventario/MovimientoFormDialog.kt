package com.acacioswork.ui.inventario

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.MovimientoRequest
import com.acacioswork.model.Producto
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Diálogo modal para registrar entradas/salidas de inventario con vencimientos y lotes.
 * @author RADJ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormDialog(
    producto: Producto,
    tipoMovimiento: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isEntrada = tipoMovimiento == "ENTRADA"

    var cantidad by remember { mutableStateOf("1") }
    var referencia by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf(LocalDate.now().plusYears(1).toString()) }
    var codigoLote by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEntrada) "📥 Registrar Entrada" else "📤 Registrar Salida",
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
                    Text(
                        text = "Producto: ${producto.nombre}",
                        color = TextLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                item {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad a transferir", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (isEntrada) {
                    item {
                        OutlinedTextField(
                            value = fechaVencimiento,
                            onValueChange = { fechaVencimiento = it },
                            label = { Text("Fecha de Vencimiento (AAAA-MM-DD)", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = codigoLote,
                            onValueChange = { codigoLote = it },
                            label = { Text("Código de Lote (Opcional)", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BgDark,
                                unfocusedContainerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = referencia,
                        onValueChange = { referencia = it },
                        label = { Text("Referencia (Ej: Factura, Proveedor)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        label = { Text("Observaciones adicionales", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgDark,
                            unfocusedContainerColor = BgDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = cantidad.toIntOrNull()
                    if (qty == null || qty <= 0) {
                        Toast.makeText(context, "La cantidad debe ser mayor a cero.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    var ref = referencia.trim()
                    var obs = observacion.trim()

                    if (isEntrada) {
                        val fv = fechaVencimiento.trim()
                        if (fv.isEmpty()) {
                            Toast.makeText(context, "La fecha de vencimiento es obligatoria para registrar una entrada.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        try {
                            LocalDate.parse(fv)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Fecha de vencimiento inválida. Formato: AAAA-MM-DD.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        ref = if (ref.isEmpty()) "Vencimiento: $fv" else "$ref [$fv]"
                        val lote = codigoLote.trim()
                        if (lote.isNotEmpty()) {
                            obs = if (obs.isEmpty()) "Lote: $lote" else "$obs [Lote: $lote]"
                        }
                    }

                    coroutineScope.launch {
                        isSaving = true
                        try {
                            val request = MovimientoRequest(
                                idProducto = producto.id ?: 0L,
                                tipoMovimiento = tipoMovimiento,
                                cantidad = qty,
                                referencia = if (ref.isEmpty()) null else ref,
                                observacion = if (obs.isEmpty()) null else obs,
                                idUsuario = 1L
                            )
                            val res = RetrofitClient.apiService.createMovimiento(request)
                            if (res.success) {
                                Toast.makeText(context, "Movimiento registrado correctamente.", Toast.LENGTH_SHORT).show()
                                onSave()
                            } else {
                                Toast.makeText(context, "Error: ${res.message}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al registrar movimiento: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isEntrada) AccentGreen else AlertRed),
                enabled = !isSaving
            ) {
                Text(text = if (isSaving) "Guardando..." else "Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(text = "Cancelar", color = TextMuted)
            }
        }
    )
}
