package com.acacioswork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa una alerta de stock mínimo alcanzado. @author RADJ */
@Data
@NoArgsConstructor
@Entity
@Table(name = "alertas_stock_minimo")
public class AlertaStockMinimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerta;

    @Column(nullable = false)
    private Long idProducto;

    @Column(columnDefinition = "TEXT")
    private String mensaje;
}
