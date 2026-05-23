/** Pruebas unitarias para VentaService. @author RADJ */
package com.acacioswork.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Venta;
import com.acacioswork.repository.ProductoRepository;
import com.acacioswork.repository.VentaRepository;

/** Clase de prueba para la lógica de negocio de ventas. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private VentaService ventaService;

    private Venta venta;
    private Producto producto;
    private DetalleVenta detalle;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Gaseosa");
        producto.setStockActual(10);
        producto.setPrecioVenta(2500.0);

        detalle = new DetalleVenta();
        detalle.setIdProducto(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(2500.0);
        detalle.setSubtotal(5000.0);

        venta = new Venta();
        venta.setId(1L);
        venta.setIdUsuario(2L);
        venta.agregarDetalle(detalle);
    }

    @Test
    void testFindAll() {
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(venta));
        List<Venta> result = ventaService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Optional<Venta> result = ventaService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveSuccessWithStock() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta saved = ventaService.save(venta);
        assertNotNull(saved);
        assertEquals(8, producto.getStockActual()); // Descontó 2 de 10
        verify(productoRepository, times(1)).findById(10L);
        verify(productoRepository, times(1)).save(producto);
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void testSaveFailProductNotFound() {
        when(productoRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ventaService.save(venta);
        });

        assertEquals("Producto no encontrado con ID: 10", exception.getMessage());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void testSaveFailInsufficientStock() {
        detalle.setCantidad(12); // Pide 12, solo hay 10
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ventaService.save(venta);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente para el producto"));
        verify(productoRepository, never()).save(any(Producto.class));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void testSaveSuccessNoDetails() {
        Venta ventaSinDetalles = new Venta();
        ventaSinDetalles.setId(2L);
        ventaSinDetalles.setIdUsuario(2L);
        ventaSinDetalles.setDetalles(null);

        when(ventaRepository.save(ventaSinDetalles)).thenReturn(ventaSinDetalles);

        Venta saved = ventaService.save(ventaSinDetalles);
        assertNotNull(saved);
        verify(productoRepository, never()).findById(anyLong());
        verify(ventaRepository, times(1)).save(ventaSinDetalles);
    }

    @Test
    void testDeleteById() {
        doNothing().when(ventaRepository).deleteById(1L);
        assertDoesNotThrow(() -> ventaService.deleteById(1L));
        verify(ventaRepository, times(1)).deleteById(1L);
    }
}
