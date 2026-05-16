package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa un proveedor de productos. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column
    private String telefono;
    
    @Column
    private String direccion;
    
    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;
    
    @Column(name = "id_tipo_documento", nullable = false)
    private Long idTipoDocumento;
    
    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;
    
    @Column(nullable = false)
    private Integer activo;

    @Column
    private String email;

    @Override
    public String toString() {
        return nombre;
    }
}