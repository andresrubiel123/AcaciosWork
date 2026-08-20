package com.acacioswork.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.Usuario;
import com.acacioswork.service.UsuarioService;
import com.acacioswork.util.ApiResponse;

/** controlador para la gestión de usuarios. @author RADJ */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    public UsuarioController(UsuarioService usuarioService, com.acacioswork.config.JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }



private final UsuarioService usuarioService;

    /** obtiene el listado de todos los usuarios. @author RADJ */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Usuario>>> getAll() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuarios obtenidos con éxito", usuarios));
    }

    /** crea un nuevo usuario en el sistema. @author RADJ */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Usuario>> create(@Valid @RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario creado", saved));
    }

    /** actualiza la información de un usuario por identificación. @author RADJ */
    @PutMapping("/{numeroDocumento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Usuario>> update(@PathVariable String numeroDocumento,
            @Valid @RequestBody Usuario details) {
        return usuarioService.findByNumeroDocumento(numeroDocumento.trim()).map(u -> {
            u.setNumeroDocumento(details.getNumeroDocumento());
            u.setNombre(details.getNombre());
            u.setApellido(details.getApellido());
            u.setTelefono(details.getTelefono());
            u.setEmail(details.getEmail());
            u.setUsuario(details.getUsuario());
            if (details.getClave() != null && !details.getClave().isEmpty()) {
                u.setClave(details.getClave());
            }
            u.setIdRol(details.getIdRol());
            u.setActivo(details.getActivo());
            Usuario updated = usuarioService.save(u);
            return ResponseEntity.ok(new ApiResponse<>(true, "Usuario actualizado correctamente", updated));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<>(false, "No existe un usuario con esa identificación", null)));
    }

    /** elimina un usuario por su número de documento. @author RADJ */
    @DeleteMapping("/{numeroDocumento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String numeroDocumento) {
        return usuarioService.findByNumeroDocumento(numeroDocumento.trim()).map(u -> {
            usuarioService.deleteById(u.getId());
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Usuario eliminado correctamente", "Número de documento: " + numeroDocumento));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<>(false, "No existe un usuario con esa identificación", null)));
    }

private final com.acacioswork.config.JwtUtil jwtUtil;

    /** endpoint para la autenticación de usuarios. @author RADJ */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.acacioswork.model.LoginResponse>> login(
            @Valid @RequestBody com.acacioswork.model.LoginRequest req) {
        return usuarioService.findByUsuario(req.getUsuario())
                .filter(u -> usuarioService.login(req.getUsuario(), req.getClave()).isPresent())
                .map(u -> {
                    String token = jwtUtil.generateToken(u.getUsuario());
                    com.acacioswork.model.LoginResponse loginResp = new com.acacioswork.model.LoginResponse(token, u);
                    return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", loginResp));
                })
                .orElse(ResponseEntity.status(401).body(new ApiResponse<>(false, "Credenciales inválidas", null)));
    }
}
