package com.acacioswork.ui.ventas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.model.Cliente
import com.acacioswork.model.Producto
import com.acacioswork.ui.clientes.ClienteFormDialog
import com.acacioswork.ui.theme.*

/**
 * Vista de Ventas POS unificada con la versión Web y por debajo del límite de 300 líneas.
 * @author RADJ / Antigravity
 */
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
    var showAddClientDialog by remember { mutableStateOf(false) }

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
            // Cabecera POS
            Text(text = "🛒 Venta de Productos", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Registra ventas rápidas con búsqueda en tiempo real", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(16.dp))

            // Buscador de productos
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    OutlinedTextField(
                        value = productSearchQuery,
                        onValueChange = { productSearchQuery = it },
                        placeholder = { Text("Escribe el nombre o código del producto...", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                        trailingIcon = {
                            if (productSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { productSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextMuted)
                                }
                            }
                        },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Sugerencias de productos
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

            // Selector de Cliente
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showClientDialog = true }
                        .background(BgCard, RoundedCornerShape(8.dp))
                        .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedCliente?.let { "Doc: ${it.numeroDocumento}" } ?: "Haga clic para asociar cliente",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Cambiar", tint = TextMuted)
                }

                Button(
                    onClick = { showAddClientDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("+ Nuevo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carrito
            Text(text = "Productos en la Venta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextLight)
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
                        Text(text = "🛒 El carrito está vacío.", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Busca y agrega productos arriba.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
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

            // Resumen de la Venta Modular
            val subtotal = cart.sumOf { it.cantidad * it.producto.precioVenta }
            VentaResumenCard(
                subtotal = subtotal,
                isLoading = isLoading,
                onClear = { viewModel.clearCart() },
                onRegister = {
                    val idUsuario = com.acacioswork.network.SessionManager.userId
                    viewModel.registrarVenta(selectedCliente?.id, idUsuario)
                },
                enabled = cart.isNotEmpty()
            )
        }

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

        if (showAddClientDialog) {
            ClienteFormDialog(
                cliente = null,
                onDismiss = { showAddClientDialog = false },
                onSave = { clientePayload ->
                    viewModel.agregarClienteRapido(clientePayload) { nuevoCliente ->
                        if (nuevoCliente != null) {
                            selectedCliente = nuevoCliente
                        }
                    }
                    showAddClientDialog = false
                }
            )
        }
    }
}
