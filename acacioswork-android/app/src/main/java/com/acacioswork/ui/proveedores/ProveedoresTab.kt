package com.acacioswork.ui.proveedores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.model.Proveedor
import com.acacioswork.ui.theme.*

/**
 * Vista de Proveedores unificada con la version Web.
 * @author RADJ / Antigravity
 */
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
            // Título y Subtítulo de Sección (Paridad Web)
            Text(
                text = "Proveedores",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Gestión de contactos y suministradores",
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Buscador y Botón "+ Nuevo" Integrado en un Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar proveedor...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgCard,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        editingProveedor = null
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("+ Nuevo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

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

        // Diálogo de Registro / Edición
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
