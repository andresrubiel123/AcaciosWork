package com.acacioswork.model;

/** Modelo de Producto para el cliente de escritorio. @author RADJ */
public class Producto {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private Integer stockActual;
    private double precioCompra;
    private double precioVenta;
    private double iva;
    private Long idCategoria;
    private Long idProveedor;
    private Integer estado;
    private Integer stockMinimo;
    private Integer stockOptimo;
    private String unidadMedida = "Unidad";

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Integer getStockOptimo() { return stockOptimo; }
    public void setStockOptimo(Integer stockOptimo) { this.stockOptimo = stockOptimo; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    @Override
    public String toString() {
        return nombre + " [" + codigoBarras + "]";
    }
}