package com.acacioswork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acacioswork.service.ReporteService;
import com.acacioswork.util.ApiResponse;

/** Controlador REST para reportes y estadísticas. @author RADJ */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /** Obtiene el total de ventas del día actual. @author RADJ */
    @GetMapping("/ventas-diarias")
    public ResponseEntity<ApiResponse<Double>> getVentasDiarias() {
        double total = reporteService.reporteVentasDiarias();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total de ventas diarias obtenido con éxito", total));
    }

    /** Obtiene el total de ganancias acumuladas. @author RADJ */
    @GetMapping("/ganancias")
    public ResponseEntity<ApiResponse<Double>> getGanancias() {
        double total = reporteService.reporteGanancias();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total de ganancias obtenido con éxito", total));
    }

    /** Obtiene el listado de productos con existencias por debajo del mínimo. @author RADJ */
    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponse<Object>> getProductosBajosStock() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos con stock bajo obtenidos con éxito", reporteService.productosBajosEnStock()));
    }
}
