/** Servicio de lógica de negocio para productos. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Producto;
import com.acacioswork.repository.ProductoRepository;

/** Servicio para la gestión de productos. @author RADJ */
@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /** Obtiene todos los productos. @author RADJ */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /** Busca un producto por ID. @author RADJ */
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    /** Guarda un producto. @author RADJ */
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    /** Elimina un producto por ID. @author RADJ */
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
