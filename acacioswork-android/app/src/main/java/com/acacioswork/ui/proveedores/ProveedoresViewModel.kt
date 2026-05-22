package com.acacioswork.ui.proveedores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.Proveedor
import com.acacioswork.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProveedoresViewModel : ViewModel() {
    private val _proveedores = MutableStateFlow<List<Proveedor>>(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = _proveedores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        cargarProveedores()
    }

    fun cargarProveedores() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.getProveedores()
                if (response.success && response.data != null) {
                    _proveedores.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al conectar con la API de proveedores."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarProveedor(proveedor: Proveedor) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.createProveedor(proveedor)
                if (response.success) {
                    cargarProveedores()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al registrar el proveedor."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarProveedor(id: Long, proveedor: Proveedor) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.updateProveedor(id, proveedor)
                if (response.success) {
                    cargarProveedores()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar el proveedor."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProveedor(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.deleteProveedor(id)
                if (response.success) {
                    cargarProveedores()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar el proveedor."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
