package com.acacioswork.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO simplificado para el inicio de sesión. @author RADJ */
@Data
@NoArgsConstructor
public class LoginRequest {
    private String usuario;
    private String clave;
}
