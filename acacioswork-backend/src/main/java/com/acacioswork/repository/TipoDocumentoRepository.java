/** Repositorio JPA para la entidad TipoDocumento. @author RADJ */
package com.acacioswork.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.TipoDocumento;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
}
