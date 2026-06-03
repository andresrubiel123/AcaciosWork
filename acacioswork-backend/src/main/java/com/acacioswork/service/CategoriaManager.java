package com.acacioswork.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acacioswork.model.Categoria;
import com.acacioswork.repository.CategoriaRepository;

/** Servicio para gestionar categorías. @author RADJ */
@Service
public class CategoriaManager {

    public CategoriaManager(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }



private final CategoriaRepository categoriaRepository;

    /** Obtiene todas las categorías. @author RADJ */
    public List<Categoria> leerTodasCategorias() {
        return categoriaRepository.findAll();
    }

    /** Crea una nueva categoría. @author RADJ */
    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /** Actualiza una categoría. @author RADJ */
    public Categoria actualizarCategoria(Long id, Categoria detalles) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(detalles.getNombre());
            return categoriaRepository.save(c);
        }).orElse(null);
    }

    /** Elimina una categoría. @author RADJ */
    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }
}