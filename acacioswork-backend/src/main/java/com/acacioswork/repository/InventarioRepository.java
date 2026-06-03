/** Repositorio JPA para la entidad Inventario. @author RADJ */
package com.acacioswork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByIdProducto(Long idProducto);
}
