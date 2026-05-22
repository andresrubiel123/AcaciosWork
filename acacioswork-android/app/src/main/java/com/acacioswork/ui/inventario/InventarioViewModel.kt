package com.acacioswork.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.Producto
import com.acacioswork.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Métricas de inventario
    private val _totalProductos = MutableStateFlow(0)
    val totalProductos: StateFlow<Int> = _totalProductos

    private val _stockBajoCount = MutableStateFlow(0)
    val stockBajoCount: StateFlow<Int> = _stockBajoCount

    private val _valorTotalInventario = MutableStateFlow(0.0)
    val valorTotalInventario: StateFlow<Double> = _valorTotalInventario

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.getProductos()
                if (response.success && response.data != null) {
                    _productos.value = response.data
                    calcularMetricas(response.data)
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al conectar con la API de inventario."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calcularMetricas(lista: List<Producto>) {
        _totalProductos.value = lista.size
        _stockBajoCount.value = lista.count { it.cantidad <= it.stockMinimo }
        _valorTotalInventario.value = lista.sumOf { it.cantidad * it.precioCompra }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.createProducto(producto)
                if (response.success) {
                    cargarProductos()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al crear el producto."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarProducto(id: Long, producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.updateProducto(id, producto)
                if (response.success) {
                    cargarProductos()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar el producto."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.deleteProducto(id)
                if (response.success) {
                    cargarProductos()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar el producto."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
