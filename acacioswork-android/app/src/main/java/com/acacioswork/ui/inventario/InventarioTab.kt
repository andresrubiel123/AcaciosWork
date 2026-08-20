package com.acacioswork.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.acacioswork.model.Producto
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Pestaña principal de Gestión de Inventario en Android.
 * Permite la administración de productos y el registro de movimientos rápidos de stock.
 * @author RADJ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioTab(
    viewModel: InventarioViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingProducto by remember { mutableStateOf<Producto?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Producto?>(null) }

    var showMovimientoDialog by remember { mutableStateOf(false) }
    var movimientoProducto by remember { mutableStateOf<Producto?>(null) }
    var movimientoTipo by remember { mutableStateOf("ENTRADA") }

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
            // Título y Subtítulo de Sección
            Text(
                text = "Inventario de Productos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Control total de existencias y precios",
                fontSize = 12.sp,
                color = TextMuted
            )

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
                    placeholder = { Text("Buscar producto...", color = TextMuted) },
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
                        editingProducto = null
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
                    Text(text = error ?: "", color = AlertRed, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                val filteredProducts = productos.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                            (it.codigoBarras?.contains(searchQuery) ?: false)
                }

                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No se encontraron productos.", color = TextMuted)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredProducts) { producto ->
                            ProductoCard(
                                producto = producto,
                                onEdit = {
                                    editingProducto = producto
                                    showDialog = true
                                },
                                onDelete = {
                                    showDeleteConfirmDialog = producto
                                },
                                onEntrada = {
                                    movimientoProducto = producto
                                    movimientoTipo = "ENTRADA"
                                    showMovimientoDialog = true
                                },
                                onSalida = {
                                    movimientoProducto = producto
                                    movimientoTipo = "SALIDA"
                                    showMovimientoDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo de Producto
        if (showDialog) {
            ProductoFormDialog(
                producto = editingProducto,
                onDismiss = { showDialog = false },
                onSave = { prod ->
                    if (editingProducto != null) {
                        editingProducto!!.id?.let { viewModel.actualizarProducto(it, prod) }
                    } else {
                        viewModel.agregarProducto(prod)
                    }
                    showDialog = false
                }
            )
        }

        // Diálogo de Movimiento rápido
        if (showMovimientoDialog && movimientoProducto != null) {
            MovimientoFormDialog(
                producto = movimientoProducto!!,
                tipoMovimiento = movimientoTipo,
                onDismiss = { showMovimientoDialog = false },
                onSave = {
                    viewModel.cargarProductos()
                    showMovimientoDialog = false
                }
            )
        }

        // Diálogo de Confirmación de Eliminación
        showDeleteConfirmDialog?.let { prod ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                containerColor = BgCard,
                title = { Text("Eliminar Producto", color = TextLight, fontWeight = FontWeight.Bold) },
                text = { Text("¿En verdad deseas eliminar ${prod.nombre}?", color = TextMuted, fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmDialog = null
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.apiService.deleteProducto(prod.id!!)
                                    if (response.success) {
                                        Toast.makeText(context, "Producto eliminado con éxito.", Toast.LENGTH_SHORT).show()
                                        viewModel.cargarProductos()
                                    } else {
                                        Toast.makeText(context, "No se puede eliminar: ${response.message}", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = null }) {
                        Text("Cancelar", color = TextMuted)
                    }
                }
            )
        }
    }
}
