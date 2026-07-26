/** repositorio jpa para la entidad tipodocumento. @author RADJ */
package com.acacioswork.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acacioswork.model.TipoDocumento;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
}
