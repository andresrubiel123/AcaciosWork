package com.acacioswork.util;

import com.acacioswork.model.Usuario;

/** Gestiona la sesión del usuario y el token JWT en la aplicación de escritorio. @author RADJ */
public class SessionManager {
    private static Usuario usuarioActual;
    private static String token;

    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static void setToken(String jwt) {
        token = jwt;
    }

    public static String getToken() {
        return token;
    }

    public static boolean isLoggedIn() {
        return usuarioActual != null && token != null;
    }

    public static void logout() {
        usuarioActual = null;
        token = null;
    }
}
