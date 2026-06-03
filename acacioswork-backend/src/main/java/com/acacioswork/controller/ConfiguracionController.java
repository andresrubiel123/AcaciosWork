package com.acacioswork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.model.Configuracion;
import com.acacioswork.service.ConfiguracionService;
import com.acacioswork.util.ApiResponse;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }



private final ConfiguracionService configuracionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Configuracion>> getConfiguracion() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Configuración obtenida", configuracionService.getConfiguracion()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Configuracion>> actualizarConfiguracion(@RequestBody Configuracion configuracion) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Configuración actualizada", configuracionService.actualizarConfiguracion(configuracion)));
    }
}
