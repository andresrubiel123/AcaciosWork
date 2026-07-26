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
        Configuracion existing = configuracionRepository.findById(1L).orElseGet(() -> {
            Configuracion newConfig = new Configuracion();
            newConfig.setId(1L);
            return newConfig;
        });

        // Copiar campos generales del negocio y ticket
        existing.setNombreEmpresa(configuracion.getNombreEmpresa());
        existing.setIdioma(configuracion.getIdioma());
        existing.setMoneda(configuracion.getMoneda());
        existing.setLectorCodigoBarras(configuracion.getLectorCodigoBarras());
        existing.setImpresoraActiva(configuracion.getImpresoraActiva());
        existing.setTicketLogotipo(configuracion.getTicketLogotipo());
        existing.setTicketEncabezado(configuracion.getTicketEncabezado());
        existing.setTicketPiePagina(configuracion.getTicketPiePagina());
        existing.setTicketAnchoMm(configuracion.getTicketAnchoMm());
        existing.setTicketAltoMm(configuracion.getTicketAltoMm());
        existing.setTicketMargenIzq(configuracion.getTicketMargenIzq());
        existing.setTicketMargenDer(configuracion.getTicketMargenDer());

        // Copiar campos de hardware (como fallback global)
        if (configuracion.getBarcodeMode() != null) existing.setBarcodeMode(configuracion.getBarcodeMode());
        if (configuracion.getBarcodePort() != null) existing.setBarcodePort(configuracion.getBarcodePort());
        if (configuracion.getScaleEnabled() != null) existing.setScaleEnabled(configuracion.getScaleEnabled());
        if (configuracion.getScaleProtocol() != null) existing.setScaleProtocol(configuracion.getScaleProtocol());
        if (configuracion.getScalePort() != null) existing.setScalePort(configuracion.getScalePort());
        if (configuracion.getScaleBaudrate() != null) existing.setScaleBaudrate(configuracion.getScaleBaudrate());
        if (configuracion.getPrinterInterface() != null) existing.setPrinterInterface(configuracion.getPrinterInterface());
        if (configuracion.getPrinterPort() != null) existing.setPrinterPort(configuracion.getPrinterPort());
        if (configuracion.getCajonConectadoImpresora() != null) existing.setCajonConectadoImpresora(configuracion.getCajonConectadoImpresora());
        if (configuracion.getCajonComando() != null) existing.setCajonComando(configuracion.getCajonComando());
        if (configuracion.getDatafonoIntegracion() != null) existing.setDatafonoIntegracion(configuracion.getDatafonoIntegracion());
        if (configuracion.getDatafonoProveedor() != null) existing.setDatafonoProveedor(configuracion.getDatafonoProveedor());
        if (configuracion.getDatafonoPuerto() != null) existing.setDatafonoPuerto(configuracion.getDatafonoPuerto());
        if (configuracion.getDatafonoTerminalId() != null) existing.setDatafonoTerminalId(configuracion.getDatafonoTerminalId());

        return configuracionRepository.save(existing);
    }
}

