package com.acacioswork.model;

public class Configuracion {
    private Long id;
    private String idioma;
    private String nombreEmpresa;
    private String moneda;
    private String lectorCodigoBarras;
    private String impresoraActiva;
    private String ticketLogotipo;
    private String ticketEncabezado;
    private String ticketPiePagina;
    private Integer ticketAnchoMm;
    private Integer ticketAltoMm;
    private Integer ticketMargenIzq;
    private Integer ticketMargenDer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    
    public String getLectorCodigoBarras() { return lectorCodigoBarras; }
    public void setLectorCodigoBarras(String lectorCodigoBarras) { this.lectorCodigoBarras = lectorCodigoBarras; }
    
    public String getImpresoraActiva() { return impresoraActiva; }
    public void setImpresoraActiva(String impresoraActiva) { this.impresoraActiva = impresoraActiva; }
    
    public String getTicketLogotipo() { return ticketLogotipo; }
    public void setTicketLogotipo(String ticketLogotipo) { this.ticketLogotipo = ticketLogotipo; }
    
    public String getTicketEncabezado() { return ticketEncabezado; }
    public void setTicketEncabezado(String ticketEncabezado) { this.ticketEncabezado = ticketEncabezado; }
    
    public String getTicketPiePagina() { return ticketPiePagina; }
    public void setTicketPiePagina(String ticketPiePagina) { this.ticketPiePagina = ticketPiePagina; }
    
    public Integer getTicketAnchoMm() { return ticketAnchoMm; }
    public void setTicketAnchoMm(Integer ticketAnchoMm) { this.ticketAnchoMm = ticketAnchoMm; }
    
    public Integer getTicketAltoMm() { return ticketAltoMm; }
    public void setTicketAltoMm(Integer ticketAltoMm) { this.ticketAltoMm = ticketAltoMm; }
    
    public Integer getTicketMargenIzq() { return ticketMargenIzq; }
    public void setTicketMargenIzq(Integer ticketMargenIzq) { this.ticketMargenIzq = ticketMargenIzq; }
    
    public Integer getTicketMargenDer() { return ticketMargenDer; }
    public void setTicketMargenDer(Integer ticketMargenDer) { this.ticketMargenDer = ticketMargenDer; }
}
