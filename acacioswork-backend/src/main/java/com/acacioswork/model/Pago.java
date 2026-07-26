package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** entidad que representa un pago asociado a una venta. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    
    @Column(nullable = false)
    private Long idVenta;
    
    @Column(nullable = false)
    private String metodoPago;
/** efectivo, crédito, débito. @author RADJ */
    
    @Column(nullable = false)
    private double monto;
    
    @Column(nullable = false)
    private double cambio;
/** para efectivo. @author RADJ */
    
    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;
/** para crédito/débito. @author RADJ */
    
    @Column(nullable = false)
    private Long idCliente;

    public void registrarPago() {
       
/** registrar pago en base de datos. @author RADJ */
    }
}