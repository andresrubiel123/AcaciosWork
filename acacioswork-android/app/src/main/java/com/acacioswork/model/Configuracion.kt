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
    var ticketMargenDer: Int = 5
)
