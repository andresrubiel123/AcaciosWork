/** Repositorio JPA para la entidad Categoria. @author RADJ */
package com.acacioswork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acacioswork.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
