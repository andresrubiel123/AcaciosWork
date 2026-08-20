/** Pruebas unitarias para ProductoController. @author RADJ */
package com.acacioswork;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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

import com.acacioswork.controller.ProductoController;
import com.acacioswork.model.Producto;
import com.acacioswork.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase de prueba para el controlador de productos. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private Producto producto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        objectMapper = TestUtils.getObjectMapper();

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Gaseosa");
        producto.setCodigoBarras("770123456789");
        producto.setStockActual(15);
        producto.setPrecioCompra(BigDecimal.valueOf(1500.0));
        producto.setPrecioVenta(BigDecimal.valueOf(2500.0));
        producto.setIva(19.0);
        producto.setUnidadMedida("Unidad");
        producto.setEstado(1);
    }

    @Test
    void testGetAll() throws Exception {
        when(productoService.findAll()).thenReturn(Arrays.asList(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Productos obtenidos"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Gaseosa"));
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(productoService.findById(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Gaseosa"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(productoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(productoService.save(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto creado con éxito"));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(productoService.findById(1L)).thenReturn(Optional.of(producto));
        when(productoService.save(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto actualizado"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        when(productoService.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoService).deleteById(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto eliminado con éxito"));
    }
}
