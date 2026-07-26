package com.acacioswork.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

    @Id
    private Long id = 1L;

    @Column(name = "idioma")
    private String idioma = "es";

    @Column(name = "nombre_empresa")
    private String nombreEmpresa = "Mi Empresa";

    @Column(name = "moneda")
    private String moneda = "COP";

    @Column(name = "lector_codigo_barras")
    private String lectorCodigoBarras = "";

    @Column(name = "impresora_activa")
    private String impresoraActiva = "";

    @Column(name = "ticket_logotipo")
    private String ticketLogotipo = "";

    @Column(name = "ticket_encabezado")
    private String ticketEncabezado = "Gracias por su compra";

    @Column(name = "ticket_pie_pagina")
    private String ticketPiePagina = "Vuelva pronto";

    @Column(name = "ticket_ancho_mm")
    private Integer ticketAnchoMm = 80;

    @Column(name = "ticket_alto_mm")
    private Integer ticketAltoMm = 297;

    @Column(name = "ticket_margen_izq")
    private Integer ticketMargenIzq = 5;

    @Column(name = "ticket_margen_der")
    private Integer ticketMargenDer = 5;

    @Column(name = "barcode_mode")
    private String barcodeMode = "KEYBOARD";

    @Column(name = "barcode_port")
    private String barcodePort = "";

    @Column(name = "scale_enabled")
    private Boolean scaleEnabled = false;

    @Column(name = "scale_protocol")
    private String scaleProtocol = "CAS";

    @Column(name = "scale_port")
    private String scalePort = "";

    @Column(name = "scale_baudrate")
    private Integer scaleBaudrate = 9600;

    @Column(name = "printer_interface")
    private String printerInterface = "SYSTEM";

    @Column(name = "printer_port")
    private String printerPort = "";

    @Column(name = "cajon_conectado_impresora")
    private Boolean cajonConectadoImpresora = true;

    @Column(name = "cajon_comando")
    private String cajonComando = "27,112,0,25,250";

    @Column(name = "datafono_integracion")
    private Boolean datafonoIntegracion = false;

    @Column(name = "datafono_proveedor")
    private String datafonoProveedor = "REDEBAN";

    @Column(name = "datafono_puerto")
    private String datafonoPuerto = "";

    @Column(name = "datafono_terminal_id")
    private String datafonoTerminalId = "";
}

