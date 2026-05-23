/** Servicio de lógica de negocio para ventas. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Producto;
import com.acacioswork.model.Venta;
import com.acacioswork.repository.ProductoRepository;
import com.acacioswork.repository.VentaRepository;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /** Recupera todas las ventas. @author RADJ */
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    /** Busca una venta por ID. @author RADJ */
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    /**
     * Registra una venta y descuenta el stock de cada producto vendido.
     * Lanza IllegalStateException si no hay stock suficiente. @author RADJ
     */
    public Venta save(Venta venta) {
        // Validar y descontar stock para cada detalle de la venta
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getIdProducto())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Producto no encontrado con ID: " + detalle.getIdProducto()));

                int nuevoStock = producto.getStockActual() - detalle.getCantidad();
                if (nuevoStock < 0) {
                    throw new IllegalStateException(
                            "Stock insuficiente para el producto \"" + producto.getNombre() +
                            "\". Stock disponible: " + producto.getStockActual() +
                            ", solicitado: " + detalle.getCantidad());
                }
                producto.setStockActual(nuevoStock);
                productoRepository.save(producto);
            }
        }
        return ventaRepository.save(venta);
    }

    /** Elimina una venta por ID. @author RADJ */
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
}
