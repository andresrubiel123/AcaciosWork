package com.acacioswork.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa un registro en el historial de ventas. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "historial_ventas")
public class HistorialVentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRecibo;
    
    @Column(nullable = false)
    private int idUsuario;
    
    @Column(nullable = false)
    private LocalDateTime horaFecha;
    
    @Column(columnDefinition = "TEXT")
    private String accion;
    
    @Column(columnDefinition = "TEXT")
    private String modificarAnular;
    
    @Column(name = "referencia_recibo_original")
    private String referenciaReciboOriginal;
    
    @Column(columnDefinition = "TEXT")
    private String devolucionMotivo;
}