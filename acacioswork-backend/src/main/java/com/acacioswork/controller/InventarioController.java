package com.acacioswork.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.AlertaStockMinimo;
import com.acacioswork.service.InventarioService;
import com.acacioswork.util.ApiResponse;

/** Controlador REST para la gestión de alertas de inventario. @author RADJ */
@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }



private final InventarioService inventarioService;

    /** Obtiene el listado de alertas por stock mínimo. @author RADJ */
    @GetMapping("/alertas")
    public ResponseEntity<ApiResponse<List<AlertaStockMinimo>>> getAlertas() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Alertas de inventario obtenidas con éxito", inventarioService.obtenerAlertas()));
    }

    /** Limpia o reinicia las alertas de inventario. @author RADJ */
    @DeleteMapping("/alertas")
    public ResponseEntity<ApiResponse<Void>> limpiarAlertas() {
        inventarioService.limpiarAlertas();
        return ResponseEntity.ok(new ApiResponse<>(true, "Alertas limpiadas con éxito", null));
    }
}
