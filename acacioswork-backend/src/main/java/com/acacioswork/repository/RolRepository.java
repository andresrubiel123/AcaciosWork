/** Repositorio JPA para la entidad Rol. @author RADJ */
package com.acacioswork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
