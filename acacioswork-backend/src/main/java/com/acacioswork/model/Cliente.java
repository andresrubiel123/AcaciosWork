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

/** Entidad que representa un cliente de la tienda. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_tipo_documento")
    private Long idTipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column
    private String telefono;
    
    @Column
    private String email;

    @Column
    private String direccion;
    
    @Column(nullable = false)
    private boolean frecuente;

    @Column(nullable = false)
    private Integer activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /** Actualiza la información del cliente. @author RADJ */
    public void actualizarInformacion() {
        // lógica de actualización
    }
}