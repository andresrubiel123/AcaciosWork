package com.acacioswork.model;

/** Modelo de Rol para el cliente de escritorio. @author RADJ */
public class Rol {
    private Long id;
    private String nombre;

    public Rol() {}

    public Rol(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}