package com.acacioswork.model;

/** Modelo de Detalle de Venta para el cliente de escritorio. @author RADJ */
public class DetalleVenta {
    private Long id;
    private Long idVenta;
    private Long idProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public DetalleVenta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "DetalleVenta [id=" + id + ", idVenta=" + idVenta + ", idProducto=" + idProducto + ", cantidad="
                + cantidad + ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + "]";
    }
}