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
public class InventarioManager {

    @Autowired
    private ProductoRepository productoRepository;

    private static final List<AlertaStockMinimo> alertas = new ArrayList<>();

    /** Crea un nuevo producto. @author RADJ */
    public boolean crearProducto(Producto producto) {
        try {
            productoRepository.save(producto);
            verificarAlerta(producto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lee un producto por ID. @author RADJ */
    public Producto leerProducto(Long idProducto) {
        return productoRepository.findById(idProducto).orElse(null);
    }

    /** Lee un producto por código de barras. @author RADJ */
    public Producto leerProductoPorCodigo(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras).orElse(null);
    }

    /** Lee todos los productos. @author RADJ */
    public List<Producto> leerTodosProductos() {
        return productoRepository.findAll();
    }

    /** Actualiza un producto. @author RADJ */
    public boolean actualizarProducto(Long idProducto, Producto nuevoProducto) {
        try {
            if (productoRepository.existsById(idProducto)) {
                nuevoProducto.setId(idProducto);
                productoRepository.save(nuevoProducto);
                verificarAlerta(nuevoProducto);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Elimina un producto por ID. @author RADJ */
    public boolean eliminarProducto(Long idProducto) {
        try {
            if (productoRepository.existsById(idProducto)) {
                productoRepository.deleteById(idProducto);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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