/** servicio de lógica de negocio para usuarios. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;

/** servicio para la gestión de usuarios y autenticación. @author RADJ */
@Service
@Transactional
public class UsuarioService {

    public UsuarioService(UsuarioRepository usuarioRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }



private final UsuarioRepository usuarioRepository;

private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /** obtiene todos los usuarios. @author RADJ */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /** busca un usuario por id. @author RADJ */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /** busca un usuario por su número de identificación. @author RADJ */
    public Optional<Usuario> findByNumeroDocumento(String numeroDocumento) {
        return usuarioRepository.findByNumeroDocumento(numeroDocumento);
    }

    /** busca un usuario por su nombre de usuario (login). @author RADJ */
    public Optional<Usuario> findByUsuario(String username) {
        return usuarioRepository.findByUsuario(username);
    }

    /** realiza la validación de credenciales para el login. @author RADJ */
    public Optional<Usuario> login(String username, String plainPassword) {

       
/** busca solo por usuario. @author RADJ */
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

           
/** 2 compara la clave encriptada con bcrypt. @author RADJ */
            if (passwordEncoder.matches(plainPassword, usuario.getClave())) {
                return Optional.of(usuario);
/** ✅ login ok. @author RADJ */
            }
        }

        return Optional.empty();
/** ❌ login fallido. @author RADJ */
    }

    /** guarda un usuario, encriptando su clave si es necesario. @author RADJ */
    public Usuario save(Usuario usuario) {
       
/** validar duplicados antes de guardar (para dar un error más claro). @author RADJ */
        if (usuario.getId() == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
                throw new RuntimeException("El nombre de usuario '" + usuario.getUsuario() + "' ya está en uso.");
            }
            if (usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento()).isPresent()) {
                throw new RuntimeException("El número de documento '" + usuario.getNumeroDocumento() + "' ya está registrado.");
            }
        }

       
/** aplica bcrypt a la clave antes de guardar para seguridad moderna. @author RADJ */
        if (usuario.getClave() != null && !usuario.getClave().startsWith("$2a$")) {
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        }
        return usuarioRepository.save(usuario);
    }

    /** elimina un usuario por su id. @author RADJ */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
