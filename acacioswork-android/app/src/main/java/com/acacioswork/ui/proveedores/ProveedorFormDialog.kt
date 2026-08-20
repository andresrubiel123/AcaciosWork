package com.acacioswork.ui.proveedores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Proveedor
import com.acacioswork.ui.theme.*

/**
 * Diálogo modular para registrar o editar un Proveedor con scroll vertical.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorFormDialog(
    proveedor: Proveedor?,
    onDismiss: () -> Unit,
    onSave: (Proveedor) -> Unit
) {
    var nombre by remember { mutableStateOf(proveedor?.nombre ?: "") }
    var numeroDocumento by remember { mutableStateOf(proveedor?.numeroDocumento ?: "") }
    var telefono by remember { mutableStateOf(proveedor?.telefono ?: "") }
    var email by remember { mutableStateOf(proveedor?.email ?: "") }
    var direccion by remember { mutableStateOf(proveedor?.direccion ?: "") }
    var cuentaBancaria by remember { mutableStateOf(proveedor?.cuentaBancaria ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (proveedor != null) "Editar Proveedor" else "Registrar Proveedor",
                color = TextLight,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = BgCard,
        text = {
            val scrollState = rememberScrollState()
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Proveedor", color = TextMuted) },
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
                OutlinedTextField(
                    value = numeroDocumento,
                    onValueChange = { numeroDocumento = it },
                    label = { Text("Número de Documento (NIT)", color = TextMuted) },
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
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono", color = TextMuted) },
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = TextMuted) },
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
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección", color = TextMuted) },
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
                OutlinedTextField(
                    value = cuentaBancaria,
                    onValueChange = { cuentaBancaria = it },
                    label = { Text("Cuenta Bancaria", color = TextMuted) },
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
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = Proveedor(
                        id = proveedor?.id,
                        nombre = nombre,
                        numeroDocumento = numeroDocumento,
                        idTipoDocumento = proveedor?.idTipoDocumento ?: 3L, // NIT por defecto
                        telefono = if (telefono.isBlank()) null else telefono,
                        email = if (email.isBlank()) null else email,
                        direccion = if (direccion.isBlank()) null else direccion,
                        cuentaBancaria = if (cuentaBancaria.isBlank()) null else cuentaBancaria,
                        activo = proveedor?.activo ?: 1
                    )
                    onSave(p)
                },
                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color(0xFFF97316),
                                androidx.compose.ui.graphics.Color(0xFFEF4444)
                            )
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
