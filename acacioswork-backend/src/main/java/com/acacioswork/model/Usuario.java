/** entidad de usuario. @author RADJ */
package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/** entidad que representa un usuario en el sistema. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_tipo_documento")
    private Long idTipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true)
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Column
    private String telefono;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String clave;

    @Column(nullable = false)
    private Integer activo;

    @Column(name = "id_rol")
    private Long idRol;
}