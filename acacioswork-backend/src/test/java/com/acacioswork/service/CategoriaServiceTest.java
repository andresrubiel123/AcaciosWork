/** Pruebas unitarias para CategoriaService. @author RADJ */
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

import com.acacioswork.model.Categoria;
import com.acacioswork.repository.CategoriaRepository;

/** Clase de prueba para la lógica de negocio de categorías. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Bebidas");
    }

    @Test
    void testFindAll() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria));
        List<Categoria> result = categoriaService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        Optional<Categoria> result = categoriaService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Bebidas", result.get().getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        Categoria saved = categoriaService.save(categoria);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testDeleteById() {
        doNothing().when(categoriaRepository).deleteById(1L);
        assertDoesNotThrow(() -> categoriaService.deleteById(1L));
        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}
