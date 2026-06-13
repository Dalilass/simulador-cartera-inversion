package com.simulador.cartera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Punto de datos para el gráfico de evolución de la cartera.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoEvolucionDTO {

    /** Fecha en formato "YYYY-MM" para mostrar en el eje X */
    private String mes;

    /** Valor simulado de la cartera en ese mes */
    private BigDecimal valor;
}
