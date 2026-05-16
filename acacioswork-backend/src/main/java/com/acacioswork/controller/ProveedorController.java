package com.acacioswork.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.Proveedor;
import com.acacioswork.service.ProveedorService;
import com.acacioswork.util.ApiResponse;

/** Controlador REST para la gestión de proveedores. @author RADJ */
@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    /** Obtiene la lista de todos los proveedores. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Proveedor>>> getAll() {
        List<Proveedor> proveedores = proveedorService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Proveedores obtenidos con éxito", proveedores));
    }

    /** Obtiene un proveedor por su ID. @author RADJ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Proveedor>> getById(@PathVariable Long id) {
        return proveedorService.findById(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "Proveedor encontrado", p)))
                .orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Proveedor no encontrado", null)));
    }

    /** Registra un nuevo proveedor. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Proveedor>> create(@RequestBody Proveedor proveedor) {
        Proveedor saved = proveedorService.save(proveedor);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Proveedor registrado con éxito", saved));
    }

    /** Actualiza la información de un proveedor existente. @author RADJ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Proveedor>> update(@PathVariable Long id, @RequestBody Proveedor details) {
        return proveedorService.findById(id).map(p -> {
            p.setNombre(details.getNombre());
            p.setNumeroDocumento(details.getNumeroDocumento());
            p.setIdTipoDocumento(details.getIdTipoDocumento());
            p.setTelefono(details.getTelefono());
            p.setDireccion(details.getDireccion());
            p.setEmail(details.getEmail());
            p.setCuentaBancaria(details.getCuentaBancaria());
            p.setActivo(details.getActivo());
            Proveedor updated = proveedorService.save(p);
            return ResponseEntity.ok(new ApiResponse<>(true, "Proveedor actualizado con éxito", updated));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<>(false, "Proveedor no encontrado para actualizar", null)));
    }

    /** Elimina un proveedor del sistema por su ID. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return proveedorService.findById(id).map(p -> {
            proveedorService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Proveedor eliminado con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Proveedor no encontrado para eliminar", null)));
    }
}
