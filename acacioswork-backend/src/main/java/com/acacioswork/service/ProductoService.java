/** servicio de lógica de negocio para productos. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.Producto;
import com.acacioswork.model.Lote;
import com.acacioswork.repository.ProductoRepository;
import com.acacioswork.repository.LoteRepository;

/** servicio para la gestión de productos. @author RADJ */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public ProductoService(ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    /** obtiene todos los productos. @author RADJ */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /** busca un producto por id. @author RADJ */
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    /** guarda un producto y sincroniza sus lotes de stock. @author RADJ */
    public Producto save(Producto producto) {
        if (producto.getStockActual() == null) {
            producto.setStockActual(0);
        }

        // Si es un producto nuevo, guardamos primero para obtener el id
        Producto saved = productoRepository.save(producto);

        // Sincronización de lotes
        List<Lote> activeLotes = loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(saved.getId());
        int sumOfLotes = activeLotes.stream().mapToInt(l -> l.getCantidadActual()).sum();

        if (producto.getStockActual() != sumOfLotes) {
            if (producto.getStockActual() > sumOfLotes) {
                // Crear lote con la diferencia
                int diff = producto.getStockActual() - sumOfLotes;
                String vDate = producto.getFechaVencimiento();
                if (vDate == null || vDate.trim().isEmpty()) {
                    vDate = java.time.LocalDate.now().plusYears(1).toString();
                }
                Lote newLote = new Lote();
                newLote.setIdProducto(saved.getId());
                newLote.setCodigoLote("AJUSTE-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
                newLote.setCantidadInicial(diff);
                newLote.setCantidadActual(diff);
                newLote.setFechaVencimiento(vDate);
                newLote.setFechaIngreso(java.time.LocalDateTime.now());
                newLote.setActivo(true);
                loteRepository.save(newLote);
            } else {
                // Descontar la diferencia de los lotes activos (FEFO)
                int diff = sumOfLotes - producto.getStockActual();
                for (Lote lote : activeLotes) {
                    if (diff <= 0) break;
                    int cant = lote.getCantidadActual();
                    if (cant <= diff) {
                        lote.setCantidadActual(0);
                        lote.setActivo(false);
                        diff -= cant;
                    } else {
                        lote.setCantidadActual(cant - diff);
                        diff = 0;
                    }
                    loteRepository.save(lote);
                }
            }
        }

        // Volver a sincronizar campos de Producto
        List<Lote> finalActiveLotes = loteRepository.findByIdProductoAndActivoTrueOrderByFechaVencimientoAsc(saved.getId());
        int finalStock = finalActiveLotes.stream().mapToInt(l -> l.getCantidadActual()).sum();
        String finalVencimiento = finalActiveLotes.stream()
                .filter(l -> l.getCantidadActual() > 0)
                .map(l -> l.getFechaVencimiento())
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        saved.setStockActual(finalStock);
        saved.setFechaVencimiento(finalVencimiento);
        return productoRepository.save(saved);
    }

    /** elimina un producto por id. @author RADJ */
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
