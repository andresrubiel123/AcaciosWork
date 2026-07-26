package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Proveedor;
import com.acacioswork.repository.ProveedorRepository;

/** servicio para gestionar proveedores. @author RADJ */
@Service
@Transactional
public class ProveedorService {

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }



private final ProveedorRepository proveedorRepository;

    /** obtiene todos los proveedores registrados. @author RADJ */
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    /** busca un proveedor por su id. @author RADJ */
    public Optional<Proveedor> findById(Long id) {
        return proveedorRepository.findById(id);
    }

    /** guarda o actualiza un proveedor. @author RADJ */
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    /** elimina un proveedor por su id. @author RADJ */
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }
}
