package com.acacioswork.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Configuracion;
import com.acacioswork.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService {

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }



private final ConfiguracionRepository configuracionRepository;

    public Configuracion getConfiguracion() {
        return configuracionRepository.findById(1L).orElseGet(() -> {
            Configuracion defaultConfig = new Configuracion();
            defaultConfig.setId(1L);
            return configuracionRepository.save(defaultConfig);
        });
    }

    @Transactional
    public Configuracion actualizarConfiguracion(Configuracion configuracion) {
        configuracion.setId(1L); // Ensure it's always ID 1
        return configuracionRepository.save(configuracion);
    }
}
