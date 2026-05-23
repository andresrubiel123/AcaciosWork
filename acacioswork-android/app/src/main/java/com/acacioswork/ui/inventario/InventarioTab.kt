package com.acacioswork.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.model.Producto
import com.acacioswork.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioTab(
    viewModel: InventarioViewModel = viewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    val totalProductos by viewModel.totalProductos.collectAsState()
    val stockBajoCount by viewModel.stockBajoCount.collectAsState()
    val valorTotal by viewModel.valorTotalInventario.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingProducto by remember { mutableStateOf<Producto?>(null) }

    val formatCurrency = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

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
            // Tarjetas de Estadísticas (Fila responsiva simulada)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadisticaCard(
                    title = "Total Prod.",
                    value = totalProductos.toString(),
                    icon = Icons.Default.ShoppingCart,
                    iconColor = Primary,
                    modifier = Modifier.weight(1f)
                )
                EstadisticaCard(
                    title = "Stock Bajo",
                    value = stockBajoCount.toString(),
                    icon = Icons.Default.Warning,
                    iconColor = AlertRed,
                    modifier = Modifier.weight(1f)
                )
                EstadisticaCard(
                    title = "Valor Total",
                    value = formatCurrency.format(valorTotal),
                    icon = Icons.Default.Info,
                    iconColor = AccentGreen,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buscador de Productos
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar producto...", color = TextMuted) },
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

            // Indicador de Carga o Error
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "", color = AlertRed, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                // Lista de Productos
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
                                    producto.id?.let { viewModel.eliminarProducto(it) }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Botón Flotante para Agregar
        FloatingActionButton(
            onClick = {
                editingProducto = null
                showDialog = true
            },
            containerColor = AccentGreen,
            contentColor = TextLight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
        }

        // Diálogo para Agregar / Editar Producto
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
    }
}

@Composable
fun EstadisticaCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1)
            Text(text = title, fontSize = 11.sp, color = TextMuted, maxLines = 1)
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val formatCurrency = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    val esStockBajo = producto.stockActual <= producto.stockMinimo

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
                Column {
                    Text(
                        text = producto.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = "Cód: ${producto.codigoBarras ?: "Sin código"} | ${producto.unidadMedida ?: "Unidad"}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                // Badge de Stock
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (esStockBajo) AlertRed.copy(alpha = 0.2f) else AccentGreen.copy(alpha = 0.2f))
                        .border(1.dp, if (esStockBajo) AlertRed else AccentGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${producto.stockActual} unidades",
                        color = if (esStockBajo) AlertRed else AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BgDark, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Precios de compra y venta
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "P. Compra", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = formatCurrency.format(producto.precioCompra),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight
                    )
                }
                Column {
                    Text(text = "IVA", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${producto.iva}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight
                    )
                }
                Column {
                    Text(text = "P. Venta", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = formatCurrency.format(producto.precioVenta),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
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
                item {
                    OutlinedTextField(
                        value = unidadMedida,
                        onValueChange = { unidadMedida = it },
                        label = { Text("Unidad de Medida", color = TextMuted) },
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
                item {
                    OutlinedTextField(
                        value = codigoBarras,
                        onValueChange = { codigoBarras = it },
                        label = { Text("Código de Barras", color = TextMuted) },
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
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Stock Actual", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                containerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockMinimo,
                            onValueChange = { stockMinimo = it },
                            label = { Text("Stock Mínimo", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                containerColor = BgDark,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockOptimo,
                            onValueChange = { stockOptimo = it },
                            label = { Text("Stock Óptimo", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                containerColor = BgDark,
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                containerColor = BgDark,
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BgDark,
                                containerColor = BgDark,
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
                        unidadMedida = if (unidadMedida.isBlank()) "Unidad" else unidadMedida
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
