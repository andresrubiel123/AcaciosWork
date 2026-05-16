// VERSIÓN CORREGIDA POR ANTIGRAVITY - SYNC OK
package com.acacioswork.controller;

import com.acacioswork.model.Cliente;
import com.acacioswork.service.ClienteService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controlador REST para la gestión de clientes. @author RADJ */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /** Obtiene el listado de todos los clientes. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> getAll() {
        List<Cliente> clientes = clienteService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Clientes obtenidos con éxito", clientes));
    }

    /** Obtiene un cliente por su ID. @author RADJ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> getById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(cliente -> ResponseEntity.ok(new ApiResponse<>(true, "Cliente encontrado", cliente)))
                .orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Cliente no encontrado", null)));
    }

    /** Registra un nuevo cliente. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> create(@RequestBody Cliente cliente) {
        Cliente saved = clienteService.save(cliente);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Cliente registrado con éxito", saved));
    }

    /** Actualiza la información de un cliente. @author RADJ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> update(@PathVariable Long id, @RequestBody Cliente details) {
        return clienteService.findById(id).map(cliente -> {
            cliente.setNumeroDocumento(details.getNumeroDocumento());
            cliente.setIdTipoDocumento(details.getIdTipoDocumento());
            cliente.setNombre(details.getNombre());
            cliente.setTelefono(details.getTelefono());
            cliente.setEmail(details.getEmail());
            cliente.setNumeroDocumento(details.getNumeroDocumento());
            cliente.setFrecuente(details.isFrecuente());
            cliente.setActivo(details.getActivo());
            cliente.setFechaActualizacion(java.time.LocalDateTime.now());
            Cliente updated = clienteService.save(cliente);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cliente actualizado con éxito", updated));
        }).orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Cliente no encontrado para actualizar", null)));
    }

    /** Elimina un cliente del sistema por su ID. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return clienteService.findById(id).map(c -> {
            clienteService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Cliente eliminado con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Cliente no encontrado para eliminar", null)));
    }
}
