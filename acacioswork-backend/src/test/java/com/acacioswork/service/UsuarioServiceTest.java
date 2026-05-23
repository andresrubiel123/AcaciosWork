/** Pruebas unitarias para UsuarioService. @author RADJ */
package com.acacioswork.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.acacioswork.model.Usuario;
import com.acacioswork.repository.UsuarioRepository;

/** Clase de prueba para la lógica de negocio de usuarios. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsuario("admin");
        usuario.setClave("passwordEncriptado");
        usuario.setNumeroDocumento("12345678");
        usuario.setEmail("admin@acacioswork.com");
        usuario.setNombre("Admin");
        usuario.setApellido("Work");
        usuario.setIdRol(1L);
        usuario.setActivo(1);
    }

    @Test
    void testFindAll() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));
        List<Usuario> result = usuarioService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsuario());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = usuarioService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsuario());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByNumeroDocumento() {
        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = usuarioService.findByNumeroDocumento("12345678");
        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getNumeroDocumento());
        verify(usuarioRepository, times(1)).findByNumeroDocumento("12345678");
    }

    @Test
    void testFindByUsuario() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = usuarioService.findByUsuario("admin");
        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsuario());
    }

    @Test
    void testLoginSuccess() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("miClavePlana", "passwordEncriptado")).thenReturn(true);

        Optional<Usuario> result = usuarioService.login("admin", "miClavePlana");
        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsuario());
    }

    @Test
    void testLoginFailureUserNotFound() {
        when(usuarioRepository.findByUsuario("no_existe")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.login("no_existe", "clave");
        assertFalse(result.isPresent());
    }

    @Test
    void testLoginFailurePasswordIncorrect() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("claveEquivocada", "passwordEncriptado")).thenReturn(false);

        Optional<Usuario> result = usuarioService.login("admin", "claveEquivocada");
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveNewUserSuccess() {
        Usuario nuevo = new Usuario();
        nuevo.setUsuario("nuevoUser");
        nuevo.setClave("nuevaClavePlana");
        nuevo.setNumeroDocumento("87654321");
        nuevo.setEmail("nuevo@acacioswork.com");

        when(usuarioRepository.findByUsuario("nuevoUser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByNumeroDocumento("87654321")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("nuevaClavePlana")).thenReturn("$2a$10$encodedPassword");
        
        Usuario guardadoMock = new Usuario();
        guardadoMock.setId(2L);
        guardadoMock.setUsuario("nuevoUser");
        guardadoMock.setClave("$2a$10$encodedPassword");
        guardadoMock.setNumeroDocumento("87654321");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardadoMock);

        Usuario saved = usuarioService.save(nuevo);
        assertNotNull(saved);
        assertEquals(2L, saved.getId());
        assertEquals("$2a$10$encodedPassword", saved.getClave());
        verify(passwordEncoder, times(1)).encode("nuevaClavePlana");
    }

    @Test
    void testSaveDuplicateUsuarioThrowsException() {
        Usuario nuevo = new Usuario();
        nuevo.setUsuario("admin"); // Ya existe
        nuevo.setClave("clave");
        nuevo.setNumeroDocumento("87654321");

        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.save(nuevo);
        });

        assertEquals("El nombre de usuario 'admin' ya está en uso.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testSaveDuplicateDocumentoThrowsException() {
        Usuario nuevo = new Usuario();
        nuevo.setUsuario("nuevoUser");
        nuevo.setClave("clave");
        nuevo.setNumeroDocumento("12345678"); // Ya existe

        when(usuarioRepository.findByUsuario("nuevoUser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.save(nuevo);
        });

        assertEquals("El número de documento '12345678' ya está registrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);
        assertDoesNotThrow(() -> usuarioService.deleteById(1L));
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
