package com.acacioswork.controller;

import com.acacioswork.model.Venta;
import com.acacioswork.service.VentaService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controlador REST estandarizado para la gestión de ventas. @author RADJ */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    /** Obtiene el listado de todas las ventas registradas. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Venta>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Ventas obtenidas con éxito", ventaService.findAll()));
    }

    /** Obtiene una venta específica por su ID. @author RADJ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Venta>> getById(@PathVariable Long id) {
        return ventaService.findById(id)
                .map(v -> ResponseEntity.ok(new ApiResponse<>(true, "Venta encontrada", v)))
                .orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Venta no encontrada", null)));
    }

    /** Registra una nueva venta en el sistema. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Venta>> create(@RequestBody Venta venta) {
        Venta saved = ventaService.save(venta);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Venta registrada con éxito", saved));
    }

    /** Elimina una venta del sistema por su ID. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return ventaService.findById(id).map(v -> {
            ventaService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Venta eliminada con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Venta no encontrada para eliminar", null)));
    }
}
