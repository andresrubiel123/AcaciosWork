package com.acacioswork.model

data class Configuracion(
    val id: Long = 1,
    var idioma: String = "es",
    var nombreEmpresa: String = "",
    var moneda: String = "COP",
    var lectorCodigoBarras: String = "",
    var impresoraActiva: String = "",
    var ticketLogotipo: String = "",
    var ticketEncabezado: String = "",
    var ticketPiePagina: String = "",
    var ticketAnchoMm: Int = 80,
    var ticketAltoMm: Int = 297,
    var ticketMargenIzq: Int = 5,
    var ticketMargenDer: Int = 5,
    
    // Hardware Fields matching backend (paridad web)
    var barcodeMode: String = "KEYBOARD",
    var barcodePort: String = "",
    var scaleEnabled: Boolean = false,
    var scaleProtocol: String = "CAS",
    var scalePort: String = "",
    var scaleBaudrate: Int = 9600,
    var printerInterface: String = "SYSTEM",
    var printerPort: String = "",
    var cajonConectadoImpresora: Boolean = true,
    var cajonComando: String = "27,112,0,25,250",
    var datafonoIntegracion: Boolean = false,
    var datafonoProveedor: String = "REDEBAN",
    var datafonoPuerto: String = "",
    var datafonoTerminalId: String = ""
)
