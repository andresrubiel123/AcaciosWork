/** servicio de lógica de negocio para movimientos de inventario. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.acacioswork.model.MovimientoInventario;
import com.acacioswork.model.Producto;
import com.acacioswork.model.TipoMovimiento;
import com.acacioswork.repository.MovimientoInventarioRepository;
import com.acacioswork.repository.ProductoRepository;

/** servicio para gestionar las operaciones de entradas, salidas y auditoría de stock usando lotes. @author RADJ */
@Service
@Transactional
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final LoteService loteService;
    private final ProductoService productoService;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository, 
                                      ProductoRepository productoRepository, 
                                      LoteService loteService,
                                      ProductoService productoService) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.loteService = loteService;
        this.productoService = productoService;
    }

    /** registra un movimiento y actualiza el stock del producto de manera transaccional usando lotes. @author RADJ */
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
       
        /** validar cantidad. @author RADJ */
        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad de unidades debe ser mayor a cero.");
        }

        /** buscar producto asociado. @author RADJ */
        Producto producto = productoRepository.findById(movimiento.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimiento.getIdProducto()));

        if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
            // Extraer fecha de vencimiento opcional de la observación o referencia
            String vDate = null;
            if (movimiento.getObservacion() != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b").matcher(movimiento.getObservacion());
                if (m.find()) {
                    vDate = m.group(1);
                }
            }
            if (vDate == null && movimiento.getReferencia() != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b").matcher(movimiento.getReferencia());
                if (m.find()) {
                    vDate = m.group(1);
                }
            }
            if (vDate == null) {
                vDate = producto.getFechaVencimiento();
            }
            if (vDate == null || vDate.trim().isEmpty()) {
                vDate = java.time.LocalDate.now().plusYears(1).toString();
            }

            // Extraer código de lote opcional si viene formateado en la observación o referencia
            String loteCode = null;
            if (movimiento.getObservacion() != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\[Lote:\\s*([^\\]]+)\\]").matcher(movimiento.getObservacion());
                if (m.find()) {
                    loteCode = m.group(1).trim();
                } else if (movimiento.getObservacion().startsWith("Lote: ")) {
                    loteCode = movimiento.getObservacion().replace("Lote: ", "").trim();
                }
            }
            if (loteCode == null && movimiento.getReferencia() != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\[Lote:\\s*([^\\]]+)\\]").matcher(movimiento.getReferencia());
                if (m.find()) {
                    loteCode = m.group(1).trim();
                }
            }

            // Crear el nuevo lote
            loteService.crearLote(producto.getId(), loteCode, movimiento.getCantidad(), vDate);

        } else if (movimiento.getTipoMovimiento() == TipoMovimiento.SALIDA) {
            // Descontar usando FEFO
            try {
                loteService.descontarStockFEFO(producto.getId(), movimiento.getCantidad());
            } catch (Exception e) {
                throw new RuntimeException("Error al registrar salida: " + e.getMessage());
            }

        } else if (movimiento.getTipoMovimiento() == TipoMovimiento.AJUSTE) {
            // Para ajuste directo, actualizamos el stock consolidado del producto a través del servicio,
            // el cual ajustará los lotes proporcionalmente.
            producto.setStockActual(movimiento.getCantidad());
            productoService.save(producto);

        } else {
            throw new RuntimeException("Tipo de movimiento no válido: " + movimiento.getTipoMovimiento());
        }

        /** guardar el registro de movimiento. @author RADJ */
        return movimientoRepository.save(movimiento);
    }

    /** obtiene el listado completo de movimientos. @author RADJ */
    public List<MovimientoInventario> findAll() {
        return movimientoRepository.findAll();
    }

    /** obtiene los movimientos asociados a un producto específico. @author RADJ */
    public List<MovimientoInventario> findByIdProducto(Long idProducto) {
        return movimientoRepository.findByIdProducto(idProducto);
    }
}
