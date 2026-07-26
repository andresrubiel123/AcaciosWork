/** servicio de lógica de negocio para tipos de documentos. @author RADJ */
package com.acacioswork.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.TipoDocumento;
import com.acacioswork.repository.TipoDocumentoRepository;

@Service
@Transactional
public class TipoDocumentoService {

    public TipoDocumentoService(TipoDocumentoRepository repository) {
        this.repository = repository;
    }



private final TipoDocumentoRepository repository;

    /** recupera todos los tipos de documentos. @author RADJ */
    public List<TipoDocumento> findAll() {
        return repository.findAll();
    }
}
