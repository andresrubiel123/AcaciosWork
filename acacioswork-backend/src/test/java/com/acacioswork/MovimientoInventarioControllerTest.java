/** Pruebas unitarias para MovimientoInventarioController. @author RADJ */
package com.acacioswork;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.acacioswork.controller.MovimientoInventarioController;
import com.acacioswork.model.MovimientoInventario;
import com.acacioswork.model.TipoMovimiento;
import com.acacioswork.service.MovimientoInventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de movimientos de inventario. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class MovimientoInventarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovimientoInventarioService movimientoService;

    @InjectMocks
    private MovimientoInventarioController movimientoController;

    private MovimientoInventario movimiento;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movimientoController).build();
        objectMapper = TestUtils.getObjectMapper();

        movimiento = new MovimientoInventario();
        movimiento.setId(1L);
        movimiento.setIdProducto(10L);
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setCantidad(5);
        movimiento.setObservacion("Lote: LOTE123 Expiracion: 2027-12-31");
    }

    @Test
    void testRegistrarSuccess() throws Exception {
        when(movimientoService.registrarMovimiento(any(MovimientoInventario.class))).thenReturn(movimiento);

        mockMvc.perform(post("/api/movimientos-inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimiento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movimiento registrado con éxito"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testRegistrarFail() throws Exception {
        when(movimientoService.registrarMovimiento(any(MovimientoInventario.class)))
                .thenThrow(new RuntimeException("Stock insuficiente para el producto"));

        mockMvc.perform(post("/api/movimientos-inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimiento)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Stock insuficiente para el producto"));
    }

    @Test
    void testGetAll() throws Exception {
        when(movimientoService.findAll()).thenReturn(Arrays.asList(movimiento));

        mockMvc.perform(get("/api/movimientos-inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void testGetByIdProducto() throws Exception {
        when(movimientoService.findByIdProducto(10L)).thenReturn(Arrays.asList(movimiento));

        mockMvc.perform(get("/api/movimientos-inventario/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].idProducto").value(10));
    }
}
