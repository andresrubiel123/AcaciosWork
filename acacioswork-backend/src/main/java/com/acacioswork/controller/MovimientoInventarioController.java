/** Controlador REST para movimientos de inventario. @author RADJ */
package com.acacioswork.controller;

import com.acacioswork.model.MovimientoInventario;
import com.acacioswork.service.MovimientoInventarioService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Controlador para exponer servicios REST relacionados con el registro y visualización de movimientos de inventario. @author RADJ */
@RestController
@RequestMapping("/api/movimientos-inventario")
@CrossOrigin(origins = "*")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService movimientoService;

    /** Registra un nuevo movimiento de inventario (Entrada o Salida). @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoInventario>> registrar(@RequestBody MovimientoInventario movimiento) {
        try {
            MovimientoInventario saved = movimientoService.registrarMovimiento(movimiento);
            return ResponseEntity.status(201).body(new ApiResponse<>(true, "Movimiento registrado con éxito", saved));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /** Obtiene la lista total de movimientos de inventario. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoInventario>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Movimientos obtenidos con éxito", movimientoService.findAll()));
    }

    /** Obtiene la lista de movimientos para un producto determinado. @author RADJ */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<ApiResponse<List<MovimientoInventario>>> getByIdProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Movimientos del producto obtenidos", movimientoService.findByIdProducto(idProducto)));
    }
}
