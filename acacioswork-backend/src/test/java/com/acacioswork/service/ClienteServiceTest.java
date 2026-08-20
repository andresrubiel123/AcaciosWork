/** Pruebas unitarias para ClienteService. @author RADJ */
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

import com.acacioswork.model.Cliente;
import com.acacioswork.repository.ClienteRepository;

/** Clase de prueba para la lógica de negocio de clientes. @author RADJ */
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Perez");
        cliente.setNumeroDocumento("10203040");
        cliente.setEmail("juan@gmail.com");
        cliente.setActivo(1);
    }

    @Test
    void testFindAll() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));
        List<Cliente> result = clienteService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        Optional<Cliente> result = clienteService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Juan Perez", result.get().getNombre());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        Cliente saved = clienteService.save(cliente);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void testDeleteById() {
        doNothing().when(clienteRepository).deleteById(1L);
        assertDoesNotThrow(() -> clienteService.deleteById(1L));
        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
