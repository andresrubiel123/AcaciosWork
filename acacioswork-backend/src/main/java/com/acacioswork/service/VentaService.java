/** servicio de lógica de negocio para ventas. @author RADJ */
package com.acacioswork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.DetalleVenta;
import com.acacioswork.model.Venta;
import com.acacioswork.repository.VentaRepository;

@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final LoteService loteService;

    public VentaService(VentaRepository ventaRepository, LoteService loteService) {
        this.ventaRepository = ventaRepository;
        this.loteService = loteService;
    }

    /** recupera todas las ventas. @author RADJ */
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    /** busca una venta por id. @author RADJ */
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    /** registra una venta, calcula el total, y descuenta el stock de cada producto vendido usando FEFO. @author RADJ */
    public Venta save(Venta venta) {
        double totalVenta = 0;
       
        /** validar y descontar stock para cada detalle de la venta. @author RADJ */
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                // Descontar stock usando la lógica FEFO de lotes
                loteService.descontarStockFEFO(detalle.getIdProducto(), detalle.getCantidad());

                /** calcular subtotal en el detalle de venta. @author RADJ */
                detalle.calcularSubtotal();
                totalVenta += detalle.getSubtotal();
            }
        }
        venta.setValorTotal(totalVenta);
        return ventaRepository.save(venta);
    }

    /** guarda una venta directamente sin alterar el stock físico de los productos. @author RADJ */
    public Venta saveOnly(Venta venta) {
        return ventaRepository.save(venta);
    }

    /** elimina una venta por id. @author RADJ */
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
}
