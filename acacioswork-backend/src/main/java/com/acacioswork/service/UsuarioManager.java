package com.acacioswork.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;

/** servicio para gestionar usuarios. @author RADJ */
@Service
@Transactional
public class UsuarioManager {

    public UsuarioManager(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }



private final UsuarioRepository usuarioRepository;

private final BCryptPasswordEncoder passwordEncoder;

    /** realiza el login de un usuario. @author RADJ */
    public Usuario login(String usuario, String clave) {
        Usuario user = usuarioRepository.findByUsuario(usuario).orElse(null);

        if (user != null && passwordEncoder.matches(clave, user.getClave())) {
            return user;
        }

        return null;
    }

    /** crea un nuevo usuario. @author RADJ */
    public boolean crearUsuario(Usuario usuario) {
        try {
           
/** encriptar contraseña antes de guardar. @author RADJ */
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));

            usuarioRepository.save(usuario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** lee un usuario por id. @author RADJ */
    public Usuario leerUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    /** lee todos los usuarios. @author RADJ */
    public List<Usuario> leerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    /** actualiza un usuario. @author RADJ */
    public boolean actualizarUsuario(Long idUsuario, Usuario nuevoUsuario) {
        try {
            if (usuarioRepository.existsById(idUsuario)) {

               
/** encriptar si se cambia la clave. @author RADJ */
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

    /** elimina un usuario por id. @author RADJ */
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