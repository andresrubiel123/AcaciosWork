package com.acacioswork.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.AlertaStockMinimo;
import com.acacioswork.model.Producto;
import com.acacioswork.repository.ProductoRepository;

/** servicio para gestionar el inventario de productos. @author RADJ */
@Service
@Transactional
public class InventarioService {

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }



private final ProductoRepository productoRepository;

    private static final List<AlertaStockMinimo> alertas = new ArrayList<>();

    /** guarda un producto y verifica alertas de stock. @author RADJ */
    public Producto save(Producto producto) {
        Producto saved = productoRepository.save(producto);
        verificarAlerta(saved);
        return saved;
    }

    /** obtiene todos los productos del inventario. @author RADJ */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /** verifica si un producto ha alcanzado su stock mínimo y genera alerta. @author RADJ */
    private void verificarAlerta(Producto producto) {
        if (producto.getStockMinimo() > 0 && producto.getStockActual() <= producto.getStockMinimo()) {
            AlertaStockMinimo alerta = new AlertaStockMinimo();
            alerta.setIdAlerta((long) (alertas.size() + 1));
            alerta.setIdProducto(producto.getId());
            alerta.setMensaje("Producto ID " + producto.getId() + ": Stock mínimo alcanzado. Stock actual: "
                    + producto.getStockActual());
            alertas.add(alerta);
        }
    }

    /** obtiene las alertas de stock mínimo. @author RADJ */
    public List<AlertaStockMinimo> obtenerAlertas() {
        return new ArrayList<>(alertas);
    }

    /** limpia las alertas acumuladas. @author RADJ */
    public void limpiarAlertas() {
        alertas.clear();
    }
}
