package com.acacioswork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** dto para la respuesta de inicio de sesión. @author RADJ */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Usuario usuario;
}
