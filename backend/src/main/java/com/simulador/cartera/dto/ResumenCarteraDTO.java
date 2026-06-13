package com.simulador.cartera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Resumen global de la cartera para el dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenCarteraDTO {

    /** Valor actual total de todos los activos en cartera */
    private BigDecimal valorTotalCartera;

    /** Dinero total invertido (compras - ventas, sin contar dividendos) */
    private BigDecimal totalInvertido;

    /** valorTotalCartera - totalInvertido + dividendosTotales */
    private BigDecimal beneficioPerdidaTotal;

    /** (beneficioPerdidaTotal / totalInvertido) * 100 */
    private BigDecimal rentabilidadTotal;

    /** Dividendos cobrados en total */
    private BigDecimal dividendosTotales;

    /** Número de activos distintos en cartera */
    private int numeroActivos;

    /** Distribución por tipo: ETF -> 45.2%, ACCION -> 30.1%, etc. */
    private Map<String, BigDecimal> distribucionPorTipo;

    /** Lista de posiciones individuales */
    private List<PosicionDTO> posiciones;

    /** Puntos para el gráfico de evolución simulada */
    private List<PuntoEvolucionDTO> evolucion;
}
