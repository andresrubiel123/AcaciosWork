package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Proveedor;
import com.acacioswork.repository.ProveedorRepository;

/** Servicio para gestionar proveedores. @author RADJ */
@Service
@Transactional
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    /** Obtiene todos los proveedores registrados. @author RADJ */
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    /** Busca un proveedor por su ID. @author RADJ */
    public Optional<Proveedor> findById(Long id) {
        return proveedorRepository.findById(id);
    }

    /** Guarda o actualiza un proveedor. @author RADJ */
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    /** Elimina un proveedor por su ID. @author RADJ */
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }
}
