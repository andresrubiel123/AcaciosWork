package com.acacioswork.ui.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.Cliente
import com.acacioswork.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {
    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        cargarClientes()
    }

    fun cargarClientes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.getClientes()
                if (response.success && response.data != null) {
                    _clientes.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al conectar con la API de clientes."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.createCliente(cliente)
                if (response.success) {
                    cargarClientes()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al registrar el cliente."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarCliente(id: Long, cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.updateCliente(id, cliente)
                if (response.success) {
                    cargarClientes()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar el cliente."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarCliente(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.deleteCliente(id)
                if (response.success) {
                    cargarClientes()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar el cliente."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
