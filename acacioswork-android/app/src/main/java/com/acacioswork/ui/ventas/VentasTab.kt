package com.acacioswork.ui.ventas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.model.Cliente
import com.acacioswork.model.Producto
import com.acacioswork.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasTab(
    viewModel: VentasViewModel = viewModel()
) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var productSearchQuery by remember { mutableStateOf("") }
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var showClientDialog by remember { mutableStateOf(false) }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            selectedCliente = null
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

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
                text = "Registrar Venta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Buscador de productos
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    OutlinedTextField(
                        value = productSearchQuery,
                        onValueChange = { productSearchQuery = it },
                        placeholder = { Text("Buscar producto por nombre o código...", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                        trailingIcon = {
                            if (productSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { productSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextMuted)
                                }
                            }
                        },
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

                    // Lista de sugerencias de productos
                    if (productSearchQuery.isNotBlank()) {
                        val matchingProducts = productos.filter {
                            it.nombre.contains(productSearchQuery, ignoreCase = true) ||
                                    (it.codigoBarras?.contains(productSearchQuery) ?: false)
                        }.take(5)

                        if (matchingProducts.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .border(1.dp, BgCard, RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = BgCard)
                            ) {
                                Column {
                                    matchingProducts.forEach { producto ->
                                        val sinStock = producto.stockActual <= 0
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = !sinStock) {
                                                    viewModel.addToCart(producto)
                                                    productSearchQuery = ""
                                                }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = producto.nombre,
                                                    color = if (sinStock) TextMuted else TextLight,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = if (sinStock) "Sin Stock" else "Stock: ${producto.stockActual} uds",
                                                    fontSize = 12.sp,
                                                    color = if (sinStock) AlertRed else TextMuted
                                                )
                                            }
                                            Text(
                                                text = com.acacioswork.util.ConfigManager.formatCurrency(producto.precioVenta),
                                                color = AccentGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Cliente y Resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClientDialog = true }
                    .background(BgCard, RoundedCornerShape(8.dp))
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                    Column {
                        Text(
                            text = selectedCliente?.nombre ?: "Cliente Genérico / Sin registrar",
                            color = TextLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedCliente?.let { "Doc: ${it.numeroDocumento}" } ?: "Haga clic para asociar cliente",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Cambiar", tint = TextMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carrito de compras
            Text(
                text = "Productos en la Venta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (cart.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(BgCard, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("El carrito está vacío", color = TextMuted, textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cart) { item ->
                        CartItemRow(
                            item = item,
                            onQuantityChange = { qty -> viewModel.updateQuantity(item.producto.id!!, qty) },
                            onRemove = { viewModel.removeFromCart(item.producto.id!!) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen de la Venta y Botones
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val subtotal = cart.sumOf { it.cantidad * it.producto.precioVenta }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", color = TextMuted)
                        Text(com.acacioswork.util.ConfigManager.formatCurrency(subtotal), color = TextLight, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = com.acacioswork.util.ConfigManager.formatCurrency(subtotal),
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearCart() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Vaciar")
                        }
                        Button(
                            onClick = {
                                // En android el id del usuario se obtiene de SessionManager
                                val idUsuario = com.acacioswork.network.SessionManager.userId
                                viewModel.registrarVenta(selectedCliente?.id, idUsuario)
                            },
                            enabled = cart.isNotEmpty() && !isLoading,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = TextLight, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Registrar Venta", color = TextLight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Dialogo para seleccionar cliente
        if (showClientDialog) {
            ClientSelectionDialog(
                clientes = clientes,
                onDismiss = { showClientDialog = false },
                onSelect = {
                    selectedCliente = it
                    showClientDialog = false
                }
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BgDark, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.producto.nombre,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    fontSize = 14.sp
                )
                Text(
                    text = "P. Unit: ${com.acacioswork.util.ConfigManager.formatCurrency(item.producto.precioVenta)}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (item.cantidad > 1) onQuantityChange(item.cantidad - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Restar", tint = Primary)
                    }
                    Text(
                        text = item.cantidad.toString(),
                        color = TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    IconButton(
                        onClick = { if (item.cantidad < item.producto.stockActual) onQuantityChange(item.cantidad + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Sumar", tint = Primary)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AlertRed)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = com.acacioswork.util.ConfigManager.formatCurrency(item.cantidad * item.producto.precioVenta),
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSelectionDialog(
    clientes: List<Cliente>,
    onDismiss: () -> Unit,
    onSelect: (Cliente?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredClientes = clientes.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.numeroDocumento.contains(searchQuery)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Seleccionar Cliente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cliente...", color = TextMuted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgDark,
                        containerColor = BgDark,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(null) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("— Venta sin cliente registrado —", color = TextMuted, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = BgDark, thickness = 1.dp)
                    }

                    items(filteredClientes) { cliente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(cliente) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(cliente.nombre, color = TextLight, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("Doc: ${cliente.numeroDocumento}", color = TextMuted, fontSize = 12.sp)
                            }
                            if (cliente.frecuente) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Frecuente", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextMuted)
                    }
                }
            }
        }
    }
}
