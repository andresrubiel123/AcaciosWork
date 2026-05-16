package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa una categoría de producto. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;

    /** Constructor con parámetros. @author RADJ */
    public Categoria(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /** Representación en cadena de la categoría. @author RADJ */
    @Override
    public String toString() {
        return nombre;
    }
}