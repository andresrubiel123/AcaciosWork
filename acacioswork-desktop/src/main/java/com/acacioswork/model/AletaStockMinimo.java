package com.acacioswork.model;



/** Entidad que representa una alerta de stock mínimo alcanzado. @author RADJ */
public class AletaStockMinimo {
    private Long idAlerta;
    private Long idProducto;
    private String mensaje;

    public AletaStockMinimo() {}

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    @Override
    public String toString() {
        return "AletaStockMinimo [idAlerta=" + idAlerta + ", idProducto=" + idProducto + ", mensaje=" + mensaje + "]";
    }
}