package com.acacioswork.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.TipoDocumento;
import com.acacioswork.service.TipoDocumentoService;
import com.acacioswork.util.ApiResponse;

/** controlador rest estandarizado para tipos de documentos. @author RADJ */
@RestController
@RequestMapping("/api/tipos-documentos")
@CrossOrigin(origins = "*")
public class TipoDocumentoController {

    public TipoDocumentoController(TipoDocumentoService service) {
        this.service = service;
    }



private final TipoDocumentoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoDocumento>>> getAll() {
        List<TipoDocumento> tipos = service.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tipos de documentos obtenidos con éxito", tipos));
    }
}
