package com.acacioswork.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/** dto simplificado para el inicio de sesión. @author RADJ */
@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
