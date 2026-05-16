package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa el inventario y movimientos de productos. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerta;
    
    @Column(name = "entrada_producto")
    private String entradaProducto;
    
    @Column(name = "salida_producto")
    private String salidaProducto;
    
    @Column(nullable = false)
    private Long idProducto;
    
    @Column(name = "cantidad_producto")
    private int cantidadProducto;
    
    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;
    
    @Column(name = "cantidad_disponible")
    private int cantidadDisponible;
    
    @Column(name = "precio_compra")
    private double precioCompra;
    
    @Column(name = "precio_venta")
    private double precioVenta;
    
    @Column(name = "utilidad_stock")
    private double utilidadStock;
    
    @Column(name = "valor_total_stock")
    private double valorTotalStock;
}