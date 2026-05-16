package com.acacioswork.service;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/** 
 * Implementación de UserDetailsService para que Spring Security pueda cargar 
 * los datos del usuario desde la base de datos.
 * @author RADJ 
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Por ahora asignamos un rol genérico basado en el idRol si existe, o ROLE_USER por defecto.
        String roleName = (usuario.getIdRol() != null && usuario.getIdRol() == 1) ? "ROLE_ADMIN" : "ROLE_USER";

        return new User(
                usuario.getUsuario(),
                usuario.getClave(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
