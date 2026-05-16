package com.acacioswork.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa una venta en el sistema. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHora;
    
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    
    @Column(name = "id_cliente", nullable = true)
    private Long idCliente;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    @Column(name = "valor_total", nullable = false)
    private double valorTotal;

    /** Configura la fecha y vincula los detalles antes de persistir. @author RADJ */
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (this.fechaHora == null) {
            this.fechaHora = java.time.LocalDateTime.now();
        }
        if (detalles != null) {
            detalles.forEach(d -> d.setVenta(this));
        }
        calcularTotal();
    }

    /** Agrega un detalle de venta y recalcula el total. @author RADJ */
    public void agregarDetalle(DetalleVenta detalle) {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        detalles.add(detalle);
        detalle.setVenta(this);
        calcularTotal();
    }

    /** Calcula el valor total de la venta sumando sus detalles. @author RADJ */
    public void calcularTotal() {
        valorTotal = 0;
        if (detalles != null) {
            for (DetalleVenta d : detalles) {
                valorTotal += d.getSubtotal();
            }
        }
    }
}