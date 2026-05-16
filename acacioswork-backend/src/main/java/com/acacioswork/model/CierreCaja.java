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

/** Entidad que representa un cierre de caja al final del día. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cierres_caja")
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCierre;
    
    @Column(nullable = false)
    private Long idUsuario;
    
    @Column(nullable = false)
    private LocalDateTime horaFecha;
    
    @Column(nullable = false)
    private String totalCierre;
}