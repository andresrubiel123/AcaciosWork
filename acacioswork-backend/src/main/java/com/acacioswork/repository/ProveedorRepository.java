/** Repositorio JPA para la entidad Proveedor. @author RADJ */
package com.acacioswork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acacioswork.model.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNumeroDocumento(String numeroDocumento);
}
