/** Pruebas unitarias para ProductoService. @author RADJ */
package com.acacioswork.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acacioswork.model.Lote;
import com.acacioswork.model.Producto;
import com.acacioswork.repository.LoteRepository;
import com.acacioswork.repository.ProductoRepository;

/** Clase de prueba para la lógica de negocio de productos. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Gaseosa");
        producto.setStockActual(10);
        producto.setPrecioVenta(BigDecimal.valueOf(2500.0));
    }

    @Test
    void testFindAll() {
        when(productoRepository.findAll()).thenReturn(Collections.singletonList(producto));
        List<Producto> result = productoService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        Optional<Producto> result = productoService.findById(1L);
        assertEquals(true, result.isPresent());
        assertEquals("Gaseosa", result.get().getNombre());
    }

    @Test
    void testSaveWithoutLoteAdjustment() {
        // Stock actual de 10 coincide con la suma de lotes activos (10)
        Lote loteMock = new Lote();
        loteMock.setId(101L);
        loteMock.setIdProducto(1L);
        loteMock.setCantidadActual(10);
        loteMock.setFechaVencimiento("2027-12-31");

        List<Lote> activeLotes = Collections.singletonList(loteMock);

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(1L))
                .thenReturn(activeLotes);

        Producto saved = productoService.save(producto);
        assertNotNull(saved);
        assertEquals(10, saved.getStockActual());
        verify(loteRepository, never()).save(any(Lote.class));
    }

    @Test
    void testSaveCreatesNewLoteWhenStockIncreases() {
        // Stock actual es 15, suma de lotes activos es 10. Debe crear un lote de ajuste con diff=5.
        producto.setStockActual(15);

        Lote loteMock = new Lote();
        loteMock.setId(101L);
        loteMock.setIdProducto(1L);
        loteMock.setCantidadActual(10);
        loteMock.setFechaVencimiento("2027-12-31");

        List<Lote> activeLotesBefore = new ArrayList<>();
        activeLotesBefore.add(loteMock);

        // Mock tras el guardado
        Lote newLoteMock = new Lote();
        newLoteMock.setId(102L);
        newLoteMock.setIdProducto(1L);
        newLoteMock.setCantidadActual(5);
        newLoteMock.setFechaVencimiento("2027-12-31");

        List<Lote> activeLotesAfter = new ArrayList<>();
        activeLotesAfter.add(loteMock);
        activeLotesAfter.add(newLoteMock);

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(1L))
                .thenReturn(activeLotesBefore) // Primera llamada en save()
                .thenReturn(activeLotesAfter);  // Segunda llamada al final de save()

        Producto saved = productoService.save(producto);
        assertNotNull(saved);
        assertEquals(15, saved.getStockActual());
        verify(loteRepository, times(1)).save(any(Lote.class));
    }

    @Test
    void testSaveDecreasesLotesWhenStockDecreases() {
        // Stock actual es 4, suma de lotes activos es 10 (lote1=6, lote2=4). Debe descontar 6 unidades FEFO.
        producto.setStockActual(4);

        Lote lote1 = new Lote();
        lote1.setId(101L);
        lote1.setCantidadActual(6);
        lote1.setFechaVencimiento("2027-12-31");
        lote1.setActivo(true);

        Lote lote2 = new Lote();
        lote2.setId(102L);
        lote2.setCantidadActual(4);
        lote2.setFechaVencimiento("2027-12-31");
        lote2.setActivo(true);

        List<Lote> activeLotesBefore = new ArrayList<>();
        activeLotesBefore.add(lote1);
        activeLotesBefore.add(lote2);

        List<Lote> activeLotesAfter = new ArrayList<>();
        activeLotesAfter.add(lote2); // lote1 se desactiva ya que su stock pasa a 0.

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(1L))
                .thenReturn(activeLotesBefore)
                .thenReturn(activeLotesAfter);

        Producto saved = productoService.save(producto);
        assertNotNull(saved);
        assertEquals(4, saved.getStockActual());
        verify(loteRepository, times(1)).save(lote1);
    }
}
