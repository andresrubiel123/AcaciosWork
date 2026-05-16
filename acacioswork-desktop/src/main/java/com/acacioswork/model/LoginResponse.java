package com.acacioswork.model;

/** DTO para la respuesta de inicio de sesión en el cliente Desktop. @author RADJ */
public class LoginResponse {
    private String token;
    private Usuario usuario;

    public LoginResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
