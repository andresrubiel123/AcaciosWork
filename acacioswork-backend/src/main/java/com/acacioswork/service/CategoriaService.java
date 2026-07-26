package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Categoria;
import com.acacioswork.repository.CategoriaRepository;

/** servicio para la gestión de categorías. @author RADJ */
@Service
@Transactional
public class CategoriaService {

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }



private final CategoriaRepository categoriaRepository;

    /** obtiene todas las categorías. @author RADJ */
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    /** busca una categoría por id. @author RADJ */
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    /** guarda una categoría. @author RADJ */
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /** elimina una categoría por id. @author RADJ */
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}
