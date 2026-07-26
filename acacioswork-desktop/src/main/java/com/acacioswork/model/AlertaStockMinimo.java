package com.acacioswork.model;

/** modelo para alertas de stock mínimo en el cliente desktop. @author RADJ */
public class AlertaStockMinimo {
    private Long idAlerta;
    private Long idProducto;
    private String mensaje;

    public AlertaStockMinimo() {}

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
