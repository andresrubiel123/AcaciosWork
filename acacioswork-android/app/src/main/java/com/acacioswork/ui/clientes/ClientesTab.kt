package com.acacioswork.ui.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import com.acacioswork.model.Cliente
import com.acacioswork.ui.inventario.EstadisticaCard
import com.acacioswork.ui.theme.*

/**
 * Vista de Clientes unificada con la version Web.
 * @author RADJ / Antigravity
 */
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

    val totalClientes = clientes.size
    val clientesActivos = clientes.count { it.activo == 1 }

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
                text = "Clientes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Base de datos de clientes registrados",
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de Estadísticas (Paridad Web: Total Clientes y Activos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadisticaCard(
                    title = "Total Clientes",
                    value = totalClientes.toString(),
                    icon = Icons.Default.Person,
                    iconColor = Primary,
                    modifier = Modifier.weight(1f)
                )
                EstadisticaCard(
                    title = "Activos",
                    value = clientesActivos.toString(),
                    icon = Icons.Default.Check,
                    iconColor = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buscador y Botón "+ Nuevo" Integrado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cliente...", color = TextMuted) },
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
                        editingCliente = null
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

        // Diálogo de Registro / Edición
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
