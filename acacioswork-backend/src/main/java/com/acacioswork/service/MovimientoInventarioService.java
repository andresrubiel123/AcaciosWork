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

/** servicio para gestionar las operaciones de entradas, salidas y auditoría de stock. @author RADJ */
@Service
@Transactional
public class MovimientoInventarioService {

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository, ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }



private final MovimientoInventarioRepository movimientoRepository;

private final ProductoRepository productoRepository;

    /** registra un movimiento y actualiza el stock del producto de manera transaccional. @author RADJ */
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
       
/** validar cantidad. @author RADJ */
        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad de unidades debe ser mayor a cero.");
        }

       
/** buscar producto asociado. @author RADJ */
        Producto producto = productoRepository.findById(movimiento.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimiento.getIdProducto()));

       
/** calcular nuevo stock. @author RADJ */
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

       
/** actualizar el stock del producto. @author RADJ */
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

       
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
