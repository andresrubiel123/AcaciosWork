/** Servicio de lógica de negocio para usuarios. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;

/** Servicio para la gestión de usuarios y autenticación. @author RADJ */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /** Obtiene todos los usuarios. @author RADJ */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /** Busca un usuario por ID. @author RADJ */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /** Busca un usuario por su número de identificación. @author RADJ */
    public Optional<Usuario> findByNumeroDocumento(String numeroDocumento) {
        return usuarioRepository.findByNumeroDocumento(numeroDocumento);
    }

    /** Busca un usuario por su nombre de usuario (login). @author RADJ */
    public Optional<Usuario> findByUsuario(String username) {
        return usuarioRepository.findByUsuario(username);
    }

    /** Realiza la validación de credenciales para el login. @author RADJ */
    public Optional<Usuario> login(String username, String plainPassword) {

        // Busca solo por usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // 2 Compara la clave encriptada con BCrypt
            if (passwordEncoder.matches(plainPassword, usuario.getClave())) {
                return Optional.of(usuario); // ✅ Login OK
            }
        }

        return Optional.empty(); // ❌ Login fallido
    }

    /** Guarda un usuario, encriptando su clave si es necesario. @author RADJ */
    public Usuario save(Usuario usuario) {
        // Validar duplicados antes de guardar (para dar un error más claro)
        if (usuario.getId() == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
                throw new RuntimeException("El nombre de usuario '" + usuario.getUsuario() + "' ya está en uso.");
            }
            if (usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento()).isPresent()) {
                throw new RuntimeException("El número de documento '" + usuario.getNumeroDocumento() + "' ya está registrado.");
            }
        }

        // Aplica BCrypt a la clave antes de guardar para seguridad moderna
        if (usuario.getClave() != null && !usuario.getClave().startsWith("$2a$")) {
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        }
        return usuarioRepository.save(usuario);
    }

    /** Elimina un usuario por su ID. @author RADJ */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
