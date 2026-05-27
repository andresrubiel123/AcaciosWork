package com.acacioswork.util

import com.acacioswork.model.Configuracion
import com.acacioswork.network.RetrofitClient
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object ConfigManager {
    var globalConfig: Configuracion? = null

    suspend fun loadConfiguracion() {
        try {
            val response = RetrofitClient.apiService.getConfiguracion()
            if (response.success) {
                globalConfig = response.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (globalConfig == null) {
                globalConfig = Configuracion() // Fallback
            }
        }
    }

    suspend fun saveConfiguracion(config: Configuracion) {
        val response = RetrofitClient.apiService.updateConfiguracion(config)
        if (response.success && response.data != null) {
            globalConfig = response.data
        } else {
            throw Exception(response.message ?: "Error al guardar configuración")
        }
    }

    fun formatCurrency(amount: Double): String {
        val moneda = globalConfig?.moneda ?: "COP"
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            format.currency = Currency.getInstance(moneda)
            format.maximumFractionDigits = 0
            format.format(amount)
        } catch (e: Exception) {
            val simpleFormat = String.format("%,.0f", amount)
            "$moneda $simpleFormat"
        }
    }
}
