package com.acacioswork.ui.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.model.Cliente
import com.acacioswork.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesTab(
    viewModel: ClientesViewModel = viewModel()
) {
    val clientes by viewModel.clientes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingCliente by remember { mutableStateOf<Cliente?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Clientes Registrados",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Buscador de Clientes
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar cliente...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BgCard,
                    containerColor = BgCard,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "", color = AlertRed)
                }
            } else {
                val filteredClientes = clientes.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                            it.numeroDocumento.contains(searchQuery) ||
                            (it.telefono?.contains(searchQuery) ?: false) ||
                            (it.email?.contains(searchQuery, ignoreCase = true) ?: false)
                }

                if (filteredClientes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay clientes registrados." else "No hay clientes que coincidan con la búsqueda.",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredClientes) { cliente ->
                            ClienteCard(
                                cliente = cliente,
                                onEdit = {
                                    editingCliente = cliente
                                    showDialog = true
                                },
                                onDelete = {
                                    cliente.id?.let { viewModel.eliminarCliente(it) }
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingCliente = null
                showDialog = true
            },
            containerColor = AccentGreen,
            contentColor = TextLight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
        }

        if (showDialog) {
            ClienteFormDialog(
                cliente = editingCliente,
                onDismiss = { showDialog = false },
                onSave = { cli ->
                    if (editingCliente != null) {
                        editingCliente!!.id?.let { viewModel.actualizarCliente(it, cli) }
                    } else {
                        viewModel.agregarCliente(cli)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun ClienteCard(
    cliente: Cliente,
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
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                    }
                    Column {
                        Text(
                            text = cliente.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Doc: ${cliente.numeroDocumento}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                if (cliente.frecuente) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGreen.copy(alpha = 0.2f))
                            .border(1.dp, AccentGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Frecuente",
                            color = AccentGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BgDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!cliente.telefono.isNullOrBlank()) {
                    Text(text = "Teléfono: ${cliente.telefono}", fontSize = 13.sp, color = TextLight)
                }
                if (!cliente.email.isNullOrBlank()) {
                    Text(text = "Email: ${cliente.email}", fontSize = 13.sp, color = TextLight)
                }
                if (!cliente.direccion.isNullOrBlank()) {
                    Text(text = "Dirección: ${cliente.direccion}", fontSize = 13.sp, color = TextLight)
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = numeroDocumento,
                    onValueChange = { numeroDocumento = it },
                    label = { Text("Número de Documento", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
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
