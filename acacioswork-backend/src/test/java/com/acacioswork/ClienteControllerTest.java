/** Pruebas unitarias para ClienteController. @author RADJ */
package com.acacioswork;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.acacioswork.controller.ClienteController;
import com.acacioswork.model.Cliente;
import com.acacioswork.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de clientes. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        objectMapper = TestUtils.getObjectMapper();

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Perez");
        cliente.setNumeroDocumento("10203040");
        cliente.setEmail("juan@gmail.com");
        cliente.setActivo(1);
    }

    @Test
    void testGetAll() throws Exception {
        when(clienteService.findAll()).thenReturn(Arrays.asList(cliente));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Clientes obtenidos con éxito"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Juan Perez"));
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(clienteService.findById(1L)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Juan Perez"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(clienteService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(clienteService.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente registrado con éxito"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(clienteService.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteService.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente actualizado con éxito"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        when(clienteService.findById(1L)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteService).deleteById(1L);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente eliminado con éxito"));
    }
}
