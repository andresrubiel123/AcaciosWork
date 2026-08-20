package com.acacioswork.util

import com.acacioswork.model.Configuracion
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConfigManagerTest {

    @Before
    fun setUp() {
        ConfigManager.globalConfig = Configuracion(
            moneda = "COP",
            nombreEmpresa = "AcaciosWork Store"
        )
    }

    @Test
    fun testFormatCurrencyCop() {
        val formatted = ConfigManager.formatCurrency(50000.0)
        assertNotNull(formatted)
        assertTrue(formatted.contains("50.000") || formatted.contains("50,000") || formatted.contains("50000"))
    }

    @Test
    fun testFormatCurrencyUsd() {
        ConfigManager.globalConfig = Configuracion(moneda = "USD")
        val formatted = ConfigManager.formatCurrency(100.0)
        assertNotNull(formatted)
        assertTrue(formatted.contains("100"))
    }

    @Test
    fun testDefaultConfiguracionValues() {
        val defaultConfig = Configuracion()
        assertEquals("COP", defaultConfig.moneda)
        assertEquals("es", defaultConfig.idioma)
        assertEquals(80, defaultConfig.ticketAnchoMm)
    }
}
