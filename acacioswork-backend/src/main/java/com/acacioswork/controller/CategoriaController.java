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

import com.acacioswork.model.Categoria;
import com.acacioswork.service.CategoriaService;
import com.acacioswork.util.ApiResponse;

/** controlador rest para la gestión de categorías. @author RADJ */
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }



private final CategoriaService categoriaService;

    /** obtiene el listado de todas las categorías. @author RADJ */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Categoria>>> getAll() {
        List<Categoria> categorias = categoriaService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Categorías obtenidas con éxito", categorias));
    }

    /** crea una nueva categoría. @author RADJ */
    @PostMapping
    public ResponseEntity<ApiResponse<Categoria>> create(@RequestBody Categoria categoria) {
        Categoria saved = categoriaService.save(categoria);
        return ResponseEntity.status(201).body(new ApiResponse<>(true, "Categoría creada con éxito", saved));
    }

    /** actualiza una categoría existente. @author RADJ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> update(@PathVariable Long id, @RequestBody Categoria detalles) {
        return categoriaService.findById(id).map(c -> {
            c.setNombre(detalles.getNombre());
            Categoria updated = categoriaService.save(c);
            return ResponseEntity.ok(new ApiResponse<>(true, "Categoría actualizada con éxito", updated));
        }).orElse(ResponseEntity.status(404).body(new ApiResponse<>(false, "Categoría no encontrada", null)));
    }

    /** elimina una categoría del sistema por su id. @author RADJ */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return categoriaService.findById(id).map(c -> {
            categoriaService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "Categoría eliminada con éxito", null));
        }).orElse(ResponseEntity.status(404)
                .body(new ApiResponse<Void>(false, "Categoría no encontrada para eliminar", null)));
    }
}
