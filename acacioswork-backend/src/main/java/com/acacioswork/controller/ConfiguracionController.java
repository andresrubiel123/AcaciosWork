package com.acacioswork.controller;

import com.acacioswork.model.Configuracion;
import com.acacioswork.service.ConfiguracionService;
import com.acacioswork.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Configuracion>> getConfiguracion() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Configuración obtenida", configuracionService.getConfiguracion()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Configuracion>> actualizarConfiguracion(@RequestBody Configuracion configuracion) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Configuración actualizada", configuracionService.actualizarConfiguracion(configuracion)));
    }
}
