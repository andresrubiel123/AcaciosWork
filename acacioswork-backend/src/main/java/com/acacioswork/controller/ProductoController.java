package com.acacioswork.controller;

import com.acacioswork.model.Producto;
import com.acacioswork.service.ProductoService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controlador REST para la gestión de productos. @author RADJ */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /** Obtiene el listado de todos los productos. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Producto>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos obtenidos", productoService.findAll()));
    }

    /** Obtiene un producto por su ID. @author RADJ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "Producto encontrado", p)))
                .orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Producto no encontrado", null)));
    }

    /** Registra un nuevo producto. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Producto>> create(@RequestBody Producto producto) {
        Producto saved = productoService.save(producto);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Producto creado con éxito", saved));
    }

    /** Actualiza la información de un producto. @author RADJ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> update(@PathVariable Long id, @RequestBody Producto details) {
        return productoService.findById(id).map(p -> {
            p.setNombre(details.getNombre());
            p.setCodigoBarras(details.getCodigoBarras());
            p.setCantidad(details.getCantidad());
            p.setPrecioCompra(details.getPrecioCompra());
            p.setPrecioVenta(details.getPrecioVenta());
            p.setIva(details.getIva());
            p.setIdCategoria(details.getIdCategoria());
            p.setIdProveedor(details.getIdProveedor());
            p.setEstado(details.getEstado());
            p.setStockMinimo(details.getStockMinimo());
            Producto updated = productoService.save(p);
            return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado", updated));
        }).orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Producto no encontrado", null)));
    }

    /** Elimina un producto del sistema por su ID. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return productoService.findById(id).map(p -> {
            productoService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Producto eliminado con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Producto no encontrado para eliminar", null)));
    }
}
