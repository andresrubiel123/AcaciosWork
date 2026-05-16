package com.acacioswork.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;

/** Servicio para gestionar usuarios. @author RADJ */
@Service
@Transactional
public class UsuarioManager {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** Realiza el login de un usuario */
    public Usuario login(String usuario, String clave) {
        Usuario user = usuarioRepository.findByUsuario(usuario).orElse(null);

        if (user != null && passwordEncoder.matches(clave, user.getClave())) {
            return user;
        }

        return null;
    }

    /** Crea un nuevo usuario */
    public boolean crearUsuario(Usuario usuario) {
        try {
            // Encriptar contraseña antes de guardar
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));

            usuarioRepository.save(usuario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lee un usuario por ID */
    public Usuario leerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    /** Lee todos los usuarios */
    public List<Usuario> leerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    /** Actualiza un usuario */
    public boolean actualizarUsuario(Long idUsuario, Usuario nuevoUsuario) {
        try {
            if (usuarioRepository.existsById(idUsuario)) {

                // Encriptar si se cambia la clave
                if (nuevoUsuario.getClave() != null && !nuevoUsuario.getClave().isEmpty()) {
                    nuevoUsuario.setClave(passwordEncoder.encode(nuevoUsuario.getClave()));
                }

                nuevoUsuario.setId(idUsuario);
                usuarioRepository.save(nuevoUsuario);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Elimina un usuario por ID */
    public boolean eliminarUsuario(Long idUsuario) {
        try {
            if (usuarioRepository.existsById(idUsuario)) {
                usuarioRepository.deleteById(idUsuario);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}