package com.acacioswork.ui.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.Cliente
import com.acacioswork.model.Producto
import com.acacioswork.model.Venta
import com.acacioswork.model.DetalleVenta
import com.acacioswork.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val producto: Producto,
    var cantidad: Int
)

class VentasViewModel : ViewModel() {
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val prodResponse = RetrofitClient.apiService.getProductos()
                if (prodResponse.success && prodResponse.data != null) {
                    _productos.value = prodResponse.data.filter { it.estado == 1 }
                }
                val cliResponse = RetrofitClient.apiService.getClientes()
                if (cliResponse.success && cliResponse.data != null) {
                    _clientes.value = cliResponse.data.filter { it.activo == 1 }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al conectar con la API de datos."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(producto: Producto) {
        val currentList = _cart.value.toMutableList()
        val existing = currentList.find { it.producto.id == producto.id }
        if (existing != null) {
            if (existing.cantidad < producto.stockActual) {
                existing.cantidad++
                _cart.value = emptyList() // Force StateFlow update
                _cart.value = currentList
            } else {
                _errorMessage.value = "No hay suficiente stock para agregar más."
            }
        } else {
            currentList.add(CartItem(producto, 1))
            _cart.value = currentList
        }
    }

    fun removeFromCart(productoId: Long) {
        val currentList = _cart.value.filter { it.producto.id != productoId }
        _cart.value = currentList
    }

    fun updateQuantity(productoId: Long, quantity: Int) {
        val currentList = _cart.value.toMutableList()
        val item = currentList.find { it.producto.id == productoId }
        if (item != null) {
            val validQty = quantity.coerceIn(1, item.producto.stockActual)
            item.cantidad = validQty
            _cart.value = emptyList() // Force StateFlow update
            _cart.value = currentList
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun registrarVenta(idCliente: Long?, idUsuario: Long?) {
        if (_cart.value.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val detalles = _cart.value.map { item ->
                    DetalleVenta(
                        idProducto = item.producto.id!!,
                        cantidad = item.cantidad,
                        precioUnitario = item.producto.precioVenta,
                        subtotal = item.cantidad * item.producto.precioVenta
                    )
                }
                val total = detalles.sumOf { it.subtotal }
                val venta = Venta(
                    idCliente = idCliente,
                    idUsuario = idUsuario,
                    valorTotal = total,
                    detalles = detalles
                )
                val response = RetrofitClient.apiService.createVenta(venta)
                if (response.success) {
                    _successMessage.value = "Venta registrada con éxito."
                    clearCart()
                    cargarDatos() // Actualiza catálogo para refrescar stocks
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al registrar la venta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
