/** repositorio jpa para la entidad pago. @author RADJ */
package com.acacioswork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByIdVenta(Long idVenta);
    List<Pago> findByIdCliente(Long idCliente);
}
