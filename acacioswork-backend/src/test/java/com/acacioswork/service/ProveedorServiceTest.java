/** Pruebas unitarias para ProveedorService. @author RADJ */
package com.acacioswork.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.acacioswork.model.Proveedor;
import com.acacioswork.repository.ProveedorRepository;

/** Clase de prueba para la lógica de negocio de proveedores. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor XYZ");
        proveedor.setNumeroDocumento("900100200");
        proveedor.setIdTipoDocumento(2L);
        proveedor.setActivo(1);
    }

    @Test
    void testFindAll() {
        when(proveedorRepository.findAll()).thenReturn(Arrays.asList(proveedor));
        List<Proveedor> result = proveedorService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        Optional<Proveedor> result = proveedorService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Proveedor XYZ", result.get().getNombre());
        verify(proveedorRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(proveedorRepository.save(proveedor)).thenReturn(proveedor);
        Proveedor saved = proveedorService.save(proveedor);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(proveedorRepository, times(1)).save(proveedor);
    }

    @Test
    void testDeleteById() {
        doNothing().when(proveedorRepository).deleteById(1L);
        assertDoesNotThrow(() -> proveedorService.deleteById(1L));
        verify(proveedorRepository, times(1)).deleteById(1L);
    }
}
