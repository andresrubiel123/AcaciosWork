/** Pruebas unitarias para MovimientoInventarioService. @author RADJ */
package com.acacioswork.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acacioswork.model.Lote;
import com.acacioswork.model.MovimientoInventario;
import com.acacioswork.model.Producto;
import com.acacioswork.model.TipoMovimiento;
import com.acacioswork.repository.MovimientoInventarioRepository;
import com.acacioswork.repository.ProductoRepository;

/** Clase de prueba para la lógica de negocio de movimientos de inventario. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private LoteService loteService;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private MovimientoInventarioService movimientoService;

    private Producto producto;
    private MovimientoInventario movimiento;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Gaseosa");
        producto.setStockActual(10);

        movimiento = new MovimientoInventario();
        movimiento.setId(1L);
        movimiento.setIdProducto(10L);
        movimiento.setCantidad(5);
    }

    @Test
    void testRegistrarMovimientoThrowsWhenCantidadInvalid() {
        movimiento.setCantidad(0);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            movimientoService.registrarMovimiento(movimiento);
        });
        assertEquals("La cantidad de unidades debe ser mayor a cero.", ex.getMessage());
    }

    @Test
    void testRegistrarMovimientoThrowsWhenProductoNotFound() {
        when(productoRepository.findById(10L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            movimientoService.registrarMovimiento(movimiento);
        });
        assertEquals("Producto no encontrado con ID: 10", ex.getMessage());
    }

    @Test
    void testRegistrarMovimientoEntradaParsesLoteAndDate() {
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setObservacion("[Lote: LOT-100] Expiracion 2027-10-15");

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(loteService.crearLote(eq(10L), eq("LOT-100"), eq(5), eq("2027-10-15"))).thenReturn(new Lote());

        MovimientoInventario saved = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(saved);
        verify(loteService, times(1)).crearLote(10L, "LOT-100", 5, "2027-10-15");
        verify(movimientoRepository, times(1)).save(movimiento);
    }

    @Test
    void testRegistrarMovimientoSalidaCallsFefo() {
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        doNothing().when(loteService).descontarStockFEFO(10L, 5);

        MovimientoInventario saved = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(saved);
        verify(loteService, times(1)).descontarStockFEFO(10L, 5);
        verify(movimientoRepository, times(1)).save(movimiento);
    }

    @Test
    void testRegistrarMovimientoAjusteSavesProduct() {
        movimiento.setTipoMovimiento(TipoMovimiento.AJUSTE);
        movimiento.setCantidad(20); // Cambiar stock actual a 20

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(movimiento);
        when(productoService.save(any(Producto.class))).thenReturn(producto);

        MovimientoInventario saved = movimientoService.registrarMovimiento(movimiento);

        assertNotNull(saved);
        assertEquals(20, producto.getStockActual());
        verify(productoService, times(1)).save(producto);
        verify(movimientoRepository, times(1)).save(movimiento);
    }
}
