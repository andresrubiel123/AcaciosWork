package com.acacioswork.config;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Lote;
import com.acacioswork.repository.ProductoRepository;
import com.acacioswork.repository.LoteRepository;

/** Componente encargado de inicializar lotes por defecto para los productos existentes que tengan stock pero no tengan lotes creados. @author RADJ / Antigravity */
@Component
public class LoteDataMigrator implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public LoteDataMigrator(ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== INICIANDO MIGRACIÓN DE LOTES DE PRODUCTOS ======");
        List<Producto> productos = productoRepository.findAll();
        int lotesCreados = 0;

        for (Producto producto : productos) {
            List<Lote> lotesExistentes = loteRepository.findByIdProducto(producto.getId());
            if (lotesExistentes.isEmpty()) {
                int stock = producto.getStockActual() != null ? producto.getStockActual() : 0;
                if (stock > 0) {
                    String vDate = producto.getFechaVencimiento();
                    if (vDate == null || vDate.trim().isEmpty()) {
                        vDate = LocalDate.now().plusYears(1).toString();
                    }

                    Lote lote = new Lote();
                    lote.setIdProducto(producto.getId());
                    lote.setCodigoLote("LOTE-INICIAL-" + producto.getId());
                    lote.setCantidadInicial(stock);
                    lote.setCantidadActual(stock);
                    lote.setFechaVencimiento(vDate);
                    lote.setFechaIngreso(LocalDateTime.now());
                    lote.setActivo(true);

                    loteRepository.save(lote);
                    lotesCreados++;

                    // Sincronizar el vencimiento en el producto si no tenía uno válido
                    if (producto.getFechaVencimiento() == null || producto.getFechaVencimiento().trim().isEmpty()) {
                        producto.setFechaVencimiento(vDate);
                        productoRepository.save(producto);
                    }
                }
            }
        }
        System.out.println("====== MIGRACIÓN DE LOTES COMPLETADA: " + lotesCreados + " lotes iniciales creados. ======");
    }
}
