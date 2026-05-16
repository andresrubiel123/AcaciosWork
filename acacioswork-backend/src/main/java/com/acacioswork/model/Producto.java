/** Entidad de Producto para persistencia en base de datos. @author RADJ */
package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** Entidad de Producto para persistencia en base de datos. @author RADJ */
@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_compra")
    private double precioCompra;

    @Column(name = "precio_venta")
    private double precioVenta;

    @Column(nullable = false)
    private double iva;

    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "id_proveedor")
    private Long idProveedor;

    @Column(name = "activo")
    private Integer estado;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    /** Verifica si el stock actual es menor o igual al mínimo. @author RADJ */
    public boolean verificarStockMinimo() {
        return cantidad <= stockMinimo;
    }

    /** Calcula el valor total de una cantidad dada basándose en el precio de venta. @author RADJ */
    public double calcularValorTotal(int cantidadCompra) {
        return precioVenta * cantidadCompra;
    }
}