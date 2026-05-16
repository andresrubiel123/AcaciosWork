package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa el detalle de una línea dentro de una venta. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    @Column(name = "id_producto", nullable = false)
    private Long idProducto;
    
    @Column(nullable = false)
    private int cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;
    
    @Column(nullable = false)
    private double subtotal;

    @Column(name = "valor_total")
    private Double valorTotal;

    /** Calcula el subtotal y valor total multiplicando cantidad por precio unitario. @author RADJ */
    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
        this.valorTotal = this.subtotal; // En el detalle, subtotal y valor_total suelen ser lo mismo
    }
}