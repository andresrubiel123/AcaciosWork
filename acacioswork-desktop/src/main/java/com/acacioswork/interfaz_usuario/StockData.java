package com.acacioswork.interfaz_usuario;

/**
 * Modelo de datos simple para representar el estado de stock de un producto.
 * @author RADJ
 */
public class StockData {
    public final int actual;
    public final int minimo;
    public final int optimo;

    public StockData(int actual, int minimo, int optimo) {
        this.actual = actual;
        this.minimo = minimo;
        this.optimo = optimo;
    }
}
