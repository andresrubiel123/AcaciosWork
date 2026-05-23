/** Pruebas para VentaController. @author RADJ */
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

import com.acacioswork.controller.VentaController;
import com.acacioswork.model.Venta;
import com.acacioswork.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de ventas. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private Venta venta;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();
        objectMapper = new ObjectMapper();

        venta = new Venta();
        venta.setId(1L);
        venta.setIdUsuario(2L);
        venta.setValorTotal(15000.0);
    }

    @Test
    void testGetAll() throws Exception {
        when(ventaService.findAll()).thenReturn(Arrays.asList(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ventas obtenidas con éxito"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].valorTotal").value(15000.0));
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(ventaService.findById(1L)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Venta encontrada"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(ventaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Venta no encontrada"));
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Venta registrada con éxito"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        when(ventaService.findById(1L)).thenReturn(Optional.of(venta));
        doNothing().when(ventaService).deleteById(1L);

        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Venta eliminada con éxito"));
    }

    @Test
    void testDeleteNotFound() throws Exception {
        when(ventaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ventas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Venta no encontrada para eliminar"));
    }
}
