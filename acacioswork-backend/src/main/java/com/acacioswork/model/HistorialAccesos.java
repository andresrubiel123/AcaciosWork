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

/** Entidad que representa un registro en el historial de accesos al sistema. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "historial_accesos")
public class HistorialAccesos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAcceso;
    
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(columnDefinition = "TEXT")
    private String evento;
}