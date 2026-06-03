package com.acacioswork.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Venta;
import com.acacioswork.service.VentaService;
import com.acacioswork.util.ApiResponse;

/** Controlador REST estandarizado para la gestión de ventas. @author RADJ */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }



private final VentaService ventaService;

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

    /**
     * Endpoint temporal para recalcular y corregir el valorTotal de todas las ventas históricas.
     * @author RADJ
     */
    @GetMapping("/fix-totals")
    public ResponseEntity<ApiResponse<String>> fixTotals() {
        List<Venta> ventas = ventaService.findAll();
        int fixedCount = 0;
        for (Venta v : ventas) {
            double total = 0;
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    d.calcularSubtotal();
                    total += d.getSubtotal();
                }
            }
            if (v.getValorTotal() != total) {
                v.setValorTotal(total);
                ventaService.saveOnly(v);
                fixedCount++;
            }
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Ventas corregidas: " + fixedCount, null));
    }
}
