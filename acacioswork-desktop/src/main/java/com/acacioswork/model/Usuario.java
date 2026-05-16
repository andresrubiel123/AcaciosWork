package com.acacioswork.model;

/** Modelo de Usuario para el cliente de escritorio. @author RADJ */
public class Usuario {
    private Long id;
    private Long idTipoDocumento;
    @com.fasterxml.jackson.annotation.JsonProperty("numeroDocumento")
    private String identificacion;
    private String nombre;
    private String apellido;
    private String email;
    private String usuario;
    private String clave;
    private Integer activo;
    private String telefono;
    private Long idRol;

    public Usuario() {}

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(Long idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
                + ", usuario=" + usuario + ", clave=" + clave + ", activo=" + activo + ", idRol=" + idRol + "]";
    }
}