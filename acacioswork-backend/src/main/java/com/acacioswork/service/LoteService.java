package com.acacioswork.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.acacioswork.model.Lote;
import com.acacioswork.model.Producto;
import com.acacioswork.repository.LoteRepository;
import com.acacioswork.repository.ProductoRepository;

/** Servicio de lógica de negocio para la gestión de lotes y fechas de vencimiento. @author RADJ / Antigravity */
@Service
@Transactional
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    /** Crea un nuevo lote para un producto y actualiza su stock e información consolidada. */
    public Lote crearLote(Long idProducto, String codigoLote, int cantidad, String fechaVencimiento) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad inicial del lote debe ser mayor a cero.");
        }
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }

        // Generar código de lote si está vacío
        String finalCodigoLote = codigoLote;
        if (finalCodigoLote == null || finalCodigoLote.trim().isEmpty()) {
            finalCodigoLote = "LOTE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                    + "-" + (int)(Math.random() * 1000);
        }

        Lote lote = new Lote();
        lote.setIdProducto(idProducto);
        lote.setCodigoLote(finalCodigoLote);
        lote.setCantidadInicial(cantidad);
        lote.setCantidadActual(cantidad);
        lote.setFechaVencimiento(fechaVencimiento.trim());
        lote.setFechaIngreso(LocalDateTime.now());
        lote.setActivo(true);

        Lote savedLote = loteRepository.save(lote);
        sincronizarProducto(idProducto);
        return savedLote;
    }

    /** Sincroniza el stock consolidado y la fecha de vencimiento más próxima de un producto basándose en sus lotes activos. */
    public void sincronizarProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));

        List<Lote> lotesActivos = loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(idProducto);

        int stockConsolidado = 0;
        String proximoVencimiento = null;

        for (Lote lote : lotesActivos) {
            if (lote.getCantidadActual() > 0) {
                stockConsolidado += lote.getCantidadActual();
                if (proximoVencimiento == null) {
                    proximoVencimiento = lote.getFechaVencimiento(); // Primer lote activo ordenado por fecha de vencimiento
                }
            }
        }

        producto.setStockActual(stockConsolidado);
        producto.setFechaVencimiento(proximoVencimiento);
        productoRepository.save(producto);
    }

    /** Descuenta stock usando la política FEFO (First Expired, First Out). */
    public void descontarStockFEFO(Long idProducto, int cantidadADescontar) {
        if (cantidadADescontar <= 0) {
            return;
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));

        List<Lote> lotesActivos = loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(idProducto);

        // Calcular total stock activo actual
        int totalDisponible = lotesActivos.stream().mapToInt(l -> l.getCantidadActual()).sum();
        if (totalDisponible < cantidadADescontar) {
            throw new IllegalStateException(
                    "Stock insuficiente para el producto \"" + producto.getNombre() + 
                    "\". Disponible consolidado: " + totalDisponible + 
                    ", solicitado: " + cantidadADescontar
            );
        }

        int restante = cantidadADescontar;
        for (Lote lote : lotesActivos) {
            if (restante <= 0) {
                break;
            }
            int actual = lote.getCantidadActual();
            if (actual > 0) {
                if (actual <= restante) {
                    lote.setCantidadActual(0);
                    lote.setActivo(false); // lote totalmente consumido
                    restante -= actual;
                } else {
                    lote.setCantidadActual(actual - restante);
                    restante = 0;
                }
                loteRepository.save(lote);
            }
        }

        sincronizarProducto(idProducto);
    }

    /** Obtiene todos los lotes de un producto. */
    public List<Lote> findByIdProducto(Long idProducto) {
        return loteRepository.findByIdProducto(idProducto);
    }

    /** Obtiene todos los lotes en el sistema. */
    public List<Lote> findAll() {
        return loteRepository.findAll();
    }
}
