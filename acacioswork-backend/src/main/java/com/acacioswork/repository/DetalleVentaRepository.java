/** Repositorio JPA para la entidad DetalleVenta. @author RADJ */
package com.acacioswork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long idVenta);
}
