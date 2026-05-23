package com.acacioswork.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acacioswork.model.AlertaStockMinimo;
import com.acacioswork.model.Producto;
import com.acacioswork.repository.ProductoRepository;

/** Servicio para gestionar el inventario de productos. @author RADJ */
@Service
@Transactional
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    private static final List<AlertaStockMinimo> alertas = new ArrayList<>();

    /** Guarda un producto y verifica alertas de stock. @author RADJ */
    public Producto save(Producto producto) {
        Producto saved = productoRepository.save(producto);
        verificarAlerta(saved);
        return saved;
    }

    /** Obtiene todos los productos del inventario. @author RADJ */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /** Verifica si un producto ha alcanzado su stock mínimo y genera alerta. @author RADJ */
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

    /** Obtiene las alertas de stock mínimo. @author RADJ */
    public List<AlertaStockMinimo> obtenerAlertas() {
        return new ArrayList<>(alertas);
    }

    /** Limpia las alertas acumuladas. @author RADJ */
    public void limpiarAlertas() {
        alertas.clear();
    }
}
