package com.acacioswork.model;

/** Modelo de Proveedor para el cliente de escritorio. @author RADJ */
public class Proveedor {
    private Long id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String cuentaBancaria;
    private Long idTipoDocumento;
    private String numeroDocumento;
    private int activo;

    private String email;

    public Proveedor() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCuentaBancaria() { return cuentaBancaria; }
    public void setCuentaBancaria(String cuentaBancaria) { this.cuentaBancaria = cuentaBancaria; }

    public Long getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(Long idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre;
    }
}