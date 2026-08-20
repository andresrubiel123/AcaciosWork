package com.acacioswork.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.acacioswork.model.Lote;
import com.acacioswork.service.LoteService;
import com.acacioswork.util.ApiResponse;

/** Controlador REST para la gestión de lotes y fechas de vencimiento. @author RADJ / Antigravity */
@RestController
@RequestMapping("/api/lotes")
@CrossOrigin(origins = "*")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    /** Obtiene el listado de todos los lotes del sistema. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Lote>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lotes obtenidos con éxito", loteService.findAll()));
    }

    /** Obtiene el listado de lotes para un producto determinado. */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<ApiResponse<List<Lote>>> getByIdProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lotes del producto obtenidos con éxito", loteService.findByIdProducto(idProducto)));
    }
}
