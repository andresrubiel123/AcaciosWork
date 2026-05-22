package com.acacioswork.model;

/** Modelo de Tipo de Documento para el cliente de escritorio. @author RADJ */
public class TipoDocumento {
    private Long id;
    private String nombre;
    private int activo;

    public TipoDocumento() {}

    public TipoDocumento(Long id, String nombre, int activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
