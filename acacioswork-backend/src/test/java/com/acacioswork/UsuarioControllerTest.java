/** Pruebas para UsuarioController. @author RADJ */
package com.acacioswork;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.acacioswork.config.JwtUtil;
import com.acacioswork.controller.UsuarioController;
import com.acacioswork.model.LoginRequest;
import com.acacioswork.model.Usuario;
import com.acacioswork.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de usuarios. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuario;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsuario("admin");
        usuario.setClave("clave123");
        usuario.setNumeroDocumento("12345678");
        usuario.setNombre("Andres");
        usuario.setApellido("Rubiel");
        usuario.setEmail("andres@acacioswork.com");
        usuario.setIdRol(1L);
        usuario.setActivo(1);
    }

    @Test
    void testGetAll() throws Exception {
        when(usuarioService.findAll()).thenReturn(Arrays.asList(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuarios obtenidos con éxito"))
                .andExpect(jsonPath("$.data[0].usuario").value("admin"));
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario creado"))
                .andExpect(jsonPath("$.data.usuario").value("admin"));
    }

    @Test
    void testCreateFailure() throws Exception {
        when(usuarioService.save(any(Usuario.class)))
                .thenThrow(new RuntimeException("El nombre de usuario 'admin' ya está en uso."));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("El nombre de usuario 'admin' ya está en uso."));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(usuarioService.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario actualizado correctamente"))
                .andExpect(jsonPath("$.data.numeroDocumento").value("12345678"));
    }

    @Test
    void testUpdateNotFound() throws Exception {
        when(usuarioService.findByNumeroDocumento("00000000")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/00000000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No existe un usuario con esa identificación"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        when(usuarioService.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deleteById(1L);

        mockMvc.perform(delete("/api/usuarios/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario eliminado correctamente"))
                .andExpect(jsonPath("$.data").value("Número de documento: 12345678"));
    }

    @Test
    void testDeleteNotFound() throws Exception {
        when(usuarioService.findByNumeroDocumento("00000000")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/00000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No existe un usuario con esa identificación"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsuario("admin");
        req.setClave("clave123");

        when(usuarioService.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(usuarioService.login("admin", "clave123")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken("admin")).thenReturn("mockedToken");

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.data.token").value("mockedToken"))
                .andExpect(jsonPath("$.data.usuario.usuario").value("admin"));
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsuario("admin");
        req.setClave("wrong_pass");

        when(usuarioService.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(usuarioService.login("admin", "wrong_pass")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }
}
