package com.acacioswork.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Proveedor;
import com.acacioswork.repository.ProveedorRepository;

/** Servicio para gestionar proveedores. @author RADJ */
@Service
@Transactional
public class ProveedorManager {

    @Autowired
    private ProveedorRepository proveedorRepository;

    /** Crea un nuevo proveedor. @author RADJ */
    public boolean crearProveedor(Proveedor proveedor) {
        try {
            proveedorRepository.save(proveedor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lee un proveedor por ID. @author RADJ */
    public Proveedor leerProveedor(Long idProveedor) {
        return proveedorRepository.findById(idProveedor).orElse(null);
    }

    /** Lee todos los proveedores. @author RADJ */
    public List<Proveedor> leerTodosProveedores() {
        return proveedorRepository.findAll();
    }

    /** Actualiza un proveedor. @author RADJ */
    public boolean actualizarProveedor(Long idProveedor, Proveedor nuevoProveedor) {
        try {
            if (proveedorRepository.existsById(idProveedor)) {
                nuevoProveedor.setId(idProveedor);
                proveedorRepository.save(nuevoProveedor);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Elimina un proveedor por ID. @author RADJ */
    public boolean eliminarProveedor(Long idProveedor) {
        try {
            if (proveedorRepository.existsById(idProveedor)) {
                proveedorRepository.deleteById(idProveedor);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}