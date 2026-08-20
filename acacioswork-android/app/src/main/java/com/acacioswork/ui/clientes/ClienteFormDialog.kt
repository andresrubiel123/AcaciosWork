package com.acacioswork.ui.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Cliente
import com.acacioswork.ui.theme.*

/**
 * Diálogo modal con scroll vertical para registrar o editar un Cliente.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteFormDialog(
    cliente: Cliente?,
    onDismiss: () -> Unit,
    onSave: (Cliente) -> Unit
) {
    var nombre by remember { mutableStateOf(cliente?.nombre ?: "") }
    var numeroDocumento by remember { mutableStateOf(cliente?.numeroDocumento ?: "") }
    var telefono by remember { mutableStateOf(cliente?.telefono ?: "") }
    var email by remember { mutableStateOf(cliente?.email ?: "") }
    var direccion by remember { mutableStateOf(cliente?.direccion ?: "") }
    var frecuente by remember { mutableStateOf(cliente?.frecuente ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (cliente != null) "Editar Cliente" else "Registrar Cliente",
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
                    label = { Text("Nombre Completo", color = TextMuted) },
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
                    label = { Text("Número de Documento", color = TextMuted) },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = frecuente,
                        onCheckedChange = { frecuente = it },
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )
                    Text(text = "Cliente Frecuente", color = TextLight, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val c = Cliente(
                        id = cliente?.id,
                        nombre = nombre,
                        numeroDocumento = numeroDocumento,
                        idTipoDocumento = cliente?.idTipoDocumento ?: 1L,
                        telefono = if (telefono.isBlank()) null else telefono,
                        email = if (email.isBlank()) null else email,
                        direccion = if (direccion.isBlank()) null else direccion,
                        frecuente = frecuente,
                        activo = cliente?.activo ?: 1
                    )
                    onSave(c)
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
