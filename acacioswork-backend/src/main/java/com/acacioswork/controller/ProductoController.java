package com.acacioswork.controller;

import java.util.List;

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

import com.acacioswork.model.Producto;
import com.acacioswork.service.ProductoService;
import com.acacioswork.util.ApiResponse;

/** controlador rest para la gestión de productos. @author RADJ */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }



private final ProductoService productoService;

    /** obtiene el listado de todos los productos. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Producto>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos obtenidos", productoService.findAll()));
    }

    /** obtiene un producto por su id. @author RADJ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> getById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "Producto encontrado", p)))
                .orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Producto no encontrado", null)));
    }

    /** registra un nuevo producto. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Producto>> create(@RequestBody Producto producto) {
        Producto saved = productoService.save(producto);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Producto creado con éxito", saved));
    }

    /** actualiza la información de un producto. @author RADJ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> update(@PathVariable Long id, @RequestBody Producto details) {
        return productoService.findById(id).map(p -> {
            p.setNombre(details.getNombre());
            p.setCodigoBarras(details.getCodigoBarras());
            p.setStockActual(details.getStockActual());
            p.setPrecioCompra(details.getPrecioCompra());
            p.setPrecioVenta(details.getPrecioVenta());
            p.setIva(details.getIva());
            p.setIdCategoria(details.getIdCategoria());
            p.setIdProveedor(details.getIdProveedor());
            p.setEstado(details.getEstado());
            p.setStockMinimo(details.getStockMinimo());
            p.setStockOptimo(details.getStockOptimo());
            p.setUnidadMedida(details.getUnidadMedida());
            p.setFechaVencimiento(details.getFechaVencimiento());
            Producto updated = productoService.save(p);
            return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado", updated));
        }).orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Producto no encontrado", null)));
    }

    /** elimina un producto del sistema por su id. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return productoService.findById(id).map(p -> {
            productoService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Producto eliminado con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Producto no encontrado para eliminar", null)));
    }
}
