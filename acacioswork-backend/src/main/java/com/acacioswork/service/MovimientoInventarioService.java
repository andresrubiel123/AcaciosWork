/** Servicio de lógica de negocio para movimientos de inventario. @author RADJ */
package com.acacioswork.service;

import com.acacioswork.model.MovimientoInventario;
import com.acacioswork.model.Producto;
import com.acacioswork.model.TipoMovimiento;
import com.acacioswork.repository.MovimientoInventarioRepository;
import com.acacioswork.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Servicio para gestionar las operaciones de entradas, salidas y auditoría de stock. @author RADJ */
@Service
@Transactional
public class MovimientoInventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /** Registra un movimiento y actualiza el stock del producto de manera transaccional. @author RADJ */
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
        // Validar cantidad
        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad de unidades debe ser mayor a cero.");
        }

        // Buscar producto asociado
        Producto producto = productoRepository.findById(movimiento.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimiento.getIdProducto()));

        // Calcular nuevo stock
        int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
        int nuevoStock;

        if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
            nuevoStock = stockActual + movimiento.getCantidad();
        } else if (movimiento.getTipoMovimiento() == TipoMovimiento.SALIDA) {
            if (stockActual < movimiento.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para realizar la salida. Stock actual: " + stockActual);
            }
            nuevoStock = stockActual - movimiento.getCantidad();
        } else if (movimiento.getTipoMovimiento() == TipoMovimiento.AJUSTE) {
            nuevoStock = movimiento.getCantidad();
        } else {
            throw new RuntimeException("Tipo de movimiento no válido: " + movimiento.getTipoMovimiento());
        }

        // Actualizar el stock del producto
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        // Guardar el registro de movimiento
        return movimientoRepository.save(movimiento);
    }

    /** Obtiene el listado completo de movimientos. @author RADJ */
    public List<MovimientoInventario> findAll() {
        return movimientoRepository.findAll();
    }

    /** Obtiene los movimientos asociados a un producto específico. @author RADJ */
    public List<MovimientoInventario> findByIdProducto(Long idProducto) {
        return movimientoRepository.findByIdProducto(idProducto);
    }
}
