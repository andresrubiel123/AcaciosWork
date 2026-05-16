package com.acacioswork.model;

/** Entidad que representa un pago asociado a una venta. @author RADJ */
public class Pago {

    private Long idPago;
    private Long idVenta;
    private String metodoPago; // Efectivo, Crédito, Débito
    private double monto;
    private double cambio; // Para efectivo
    private String numeroTarjeta; // Para crédito/débito
    private Long idCliente;

    public Pago() {}

    // Getters y Setters
    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return "Pago [idPago=" + idPago + ", idVenta=" + idVenta + ", metodoPago=" + metodoPago + ", monto=" + monto
                + ", cambio=" + cambio + ", numeroTarjeta=" + numeroTarjeta + ", idCliente=" + idCliente + "]";
    }
}