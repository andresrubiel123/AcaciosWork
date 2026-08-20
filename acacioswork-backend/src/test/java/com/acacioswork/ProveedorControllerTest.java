/** Pruebas unitarias para ProveedorController. @author RADJ */
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

import com.acacioswork.controller.ProveedorController;
import com.acacioswork.model.Proveedor;
import com.acacioswork.service.ProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de proveedores. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ProveedorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProveedorService proveedorService;

    @InjectMocks
    private ProveedorController proveedorController;

    private Proveedor proveedor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(proveedorController).build();
        objectMapper = TestUtils.getObjectMapper();

        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor XYZ");
        proveedor.setNumeroDocumento("900100200");
        proveedor.setIdTipoDocumento(2L);
        proveedor.setActivo(1);
    }

    @Test
    void testGetAll() throws Exception {
        when(proveedorService.findAll()).thenReturn(Arrays.asList(proveedor));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedores obtenidos con éxito"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Proveedor XYZ"));
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(proveedorService.findById(1L)).thenReturn(Optional.of(proveedor));

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Proveedor XYZ"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(proveedorService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/proveedores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Proveedor no encontrado"));
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(proveedorService.save(any(Proveedor.class))).thenReturn(proveedor);

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor registrado con éxito"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(proveedorService.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorService.save(any(Proveedor.class))).thenReturn(proveedor);

        mockMvc.perform(put("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor actualizado con éxito"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        when(proveedorService.findById(1L)).thenReturn(Optional.of(proveedor));
        doNothing().when(proveedorService).deleteById(1L);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor eliminado con éxito"));
    }
}
