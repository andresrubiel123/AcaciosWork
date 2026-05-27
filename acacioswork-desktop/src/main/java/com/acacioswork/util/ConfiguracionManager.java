package com.acacioswork.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import com.acacioswork.model.Configuracion;

public class ConfiguracionManager {
    
    private static Configuracion configuracionGlobal = null;
    
    public static void loadConfiguracion() {
        try {
            configuracionGlobal = ApiClient.get("/configuracion", Configuracion.class);
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración global: " + e.getMessage());
            // Fallback default config
            configuracionGlobal = new Configuracion();
            configuracionGlobal.setMoneda("COP");
        }
    }
    
    public static Configuracion getConfiguracion() {
        if (configuracionGlobal == null) {
            loadConfiguracion();
        }
        return configuracionGlobal;
    }
    
    public static void saveConfiguracion(Configuracion nuevaConfig) throws Exception {
        configuracionGlobal = ApiClient.put("/configuracion", nuevaConfig, Configuracion.class);
    }
    
    public static String formatCurrency(double amount) {
        String moneda = getConfiguracion() != null && getConfiguracion().getMoneda() != null 
                        ? getConfiguracion().getMoneda() 
                        : "COP";
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
            format.setCurrency(Currency.getInstance(moneda));
            format.setMaximumFractionDigits(0);
            return format.format(amount);
        } catch (Exception e) {
            return moneda + " " + String.format("%,.0f", amount);
        }
    }
}
