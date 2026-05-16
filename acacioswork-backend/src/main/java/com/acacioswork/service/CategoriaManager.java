package com.acacioswork.service;

import com.acacioswork.model.Categoria;
import com.acacioswork.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servicio para gestionar categorías. @author RADJ */
@Service
public class CategoriaManager {

    @Autowired
    private CategoriaRepository categoriaRepository;

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