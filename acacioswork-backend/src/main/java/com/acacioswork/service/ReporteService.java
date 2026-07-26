package com.acacioswork.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Producto;
import com.acacioswork.model.Venta;
import com.acacioswork.repository.VentaRepository;

/** servicio para la generación de reportes y gestión de ventas. @author RADJ */
@Service
@Transactional
public class ReporteService {

    public ReporteService(VentaRepository ventaRepository, InventarioService inventarioService) {
        this.ventaRepository = ventaRepository;
        this.inventarioService = inventarioService;
    }



private final VentaRepository ventaRepository;

private final InventarioService inventarioService;

    /** registra una venta en el sistema. @author RADJ */
    public Venta saveVenta(Venta venta) {
        if (venta.getFechaHora() == null) {
            venta.setFechaHora(LocalDateTime.now());
        }
        return ventaRepository.save(venta);
    }

    /** calcula el total de ventas realizadas. @author RADJ */
    public double reporteVentasDiarias() {
        List<Venta> ventas = ventaRepository.findAll();
        return ventas.stream().mapToDouble(Venta::getValorTotal).sum();
    }

    /** reporte de ganancias brutas. @author RADJ */
    public double reporteGanancias() {
        return reporteVentasDiarias();
    }

    /** lista de productos con stock por debajo del mínimo. @author RADJ */
    public List<Producto> productosBajosEnStock() {
        List<Producto> bajos = new ArrayList<>();
        for (Producto p : inventarioService.findAll()) {
            if (p.getStockMinimo() > 0 && p.getStockActual() <= p.getStockMinimo()) {
                bajos.add(p);
            }
        }
        return bajos;
    }

    /** recupera el historial completo de ventas. @author RADJ */
    public List<Venta> findAllVentas() {
        return ventaRepository.findAll();
    }
}
