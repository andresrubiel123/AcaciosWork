/** repositorio jpa para la entidad venta. @author RADJ */
package com.acacioswork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByIdUsuario(Long idUsuario);
    List<Venta> findByIdCliente(Long idCliente);
}
