/** servicio de lógica de negocio para productos. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Producto;
import com.acacioswork.repository.ProductoRepository;

/** servicio para la gestión de productos. @author RADJ */
@Service
@Transactional
public class ProductoService {

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }



private final ProductoRepository productoRepository;

    /** obtiene todos los productos. @author RADJ */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /** busca un producto por id. @author RADJ */
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    /** guarda un producto. @author RADJ */
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    /** elimina un producto por id. @author RADJ */
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
