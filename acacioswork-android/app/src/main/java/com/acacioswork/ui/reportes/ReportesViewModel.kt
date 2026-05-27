package com.acacioswork.ui.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacioswork.model.Venta
import com.acacioswork.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/** ViewModel para gestionar datos del gráfico de reportes en Android. @author RADJ */
class ReportesViewModel : ViewModel() {

    private val _monthlySales = MutableStateFlow<List<Double>>(List(12) { 0.0 })
    val monthlySales: StateFlow<List<Double>> = _monthlySales

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        cargarVentas()
    }

    /** Carga y agrupa las ventas de forma asíncrona desde la API. @author RADJ */
    fun cargarVentas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.getVentas()
                if (response.success && response.data != null) {
                    procesarVentas(response.data)
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al conectar con la API de ventas."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Procesa y agrupa las ventas del año actual por mes. @author RADJ */
    private fun procesarVentas(ventas: List<Venta>) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val monthlyData = DoubleArray(12) { 0.0 }

        ventas.forEach { v ->
            v.fechaHora?.let { dateStr ->
                try {
                    // El formato de fechaHora es ISO (Ej: 2026-05-12T17:24:32)
                    // Usamos un parser básico para extraer año y mes de forma rápida
                    val parts = dateStr.split("T")
                    if (parts.isNotEmpty()) {
                        val dateParts = parts[0].split("-")
                        if (dateParts.size == 3) {
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt() - 1 // 1-12 -> 0-11
                            if (year == currentYear && month in 0..11) {
                                // Aplicar fallback si valorTotal es 0.0
                                var total = v.valorTotal
                                if (total == 0.0 && v.detalles.isNotEmpty()) {
                                    total = v.detalles.sumOf { it.cantidad * it.precioUnitario }
                                }
                                monthlyData[month] += total
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        _monthlySales.value = monthlyData.toList()
    }
}
