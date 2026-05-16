package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Categoria;
import com.acacioswork.repository.CategoriaRepository;

/** Servicio para la gestión de categorías. @author RADJ */
@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /** Obtiene todas las categorías. @author RADJ */
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    /** Busca una categoría por ID. @author RADJ */
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    /** Guarda una categoría. @author RADJ */
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /** Elimina una categoría por ID. @author RADJ */
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}
