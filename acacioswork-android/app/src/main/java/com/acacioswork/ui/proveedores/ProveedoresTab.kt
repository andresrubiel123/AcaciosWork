package com.acacioswork.ui.proveedores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.acacioswork.model.Proveedor
import com.acacioswork.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedoresTab(
    viewModel: ProveedoresViewModel = viewModel()
) {
    val proveedores by viewModel.proveedores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingProveedor by remember { mutableStateOf<Proveedor?>(null) }
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
                text = "Proveedores",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Buscador de Proveedores
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar proveedor...", color = TextMuted) },
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
                val filteredProveedores = proveedores.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                            it.numeroDocumento.contains(searchQuery) ||
                            (it.telefono?.contains(searchQuery) ?: false) ||
                            (it.email?.contains(searchQuery, ignoreCase = true) ?: false)
                }

                if (filteredProveedores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay proveedores registrados." else "No hay proveedores que coincidan con la búsqueda.",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredProveedores) { proveedor ->
                            ProveedorCard(
                                proveedor = proveedor,
                                onEdit = {
                                    editingProveedor = proveedor
                                    showDialog = true
                                },
                                onDelete = {
                                    proveedor.id?.let { viewModel.eliminarProveedor(it) }
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingProveedor = null
                showDialog = true
            },
            containerColor = AccentGreen,
            contentColor = TextLight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Proveedor")
        }

        if (showDialog) {
            ProveedorFormDialog(
                proveedor = editingProveedor,
                onDismiss = { showDialog = false },
                onSave = { prov ->
                    if (editingProveedor != null) {
                        editingProveedor!!.id?.let { viewModel.actualizarProveedor(it, prov) }
                    } else {
                        viewModel.agregarProveedor(prov)
                    }
                    showDialog = false
                }
            )
        }
    }
}

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
            Divider(color = BgDark, thickness = 1.dp)
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Proveedor", color = TextMuted) },
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
                    label = { Text("Número de Documento (NIT)", color = TextMuted) },
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
                OutlinedTextField(
                    value = cuentaBancaria,
                    onValueChange = { cuentaBancaria = it },
                    label = { Text("Cuenta Bancaria", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
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
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
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
