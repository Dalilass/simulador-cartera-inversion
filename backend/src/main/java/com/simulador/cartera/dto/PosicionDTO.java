package com.simulador.cartera.dto;

import com.simulador.cartera.enums.TipoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa la posición actual de un activo dentro de la cartera.
 * Se calcula en tiempo real a partir de las operaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosicionDTO {

    private Long activoId;
    private String ticker;
    private String nombre;
    private TipoActivo tipo;
    private String sector;
    private String moneda;

    /** Número de unidades que el usuario posee actualmente */
    private BigDecimal cantidadActual;

    /** Precio medio de compra ponderado */
    private BigDecimal precioMedioCompra;

    /** Precio actual del activo */
    private BigDecimal precioActual;

    /** cantidadActual * precioActual */
    private BigDecimal valorActual;

    /** cantidadActual * precioMedioCompra */
    private BigDecimal costeTotal;

    /** valorActual - costeTotal */
    private BigDecimal beneficioPerdida;

    /** (beneficioPerdida / costeTotal) * 100 */
    private BigDecimal rentabilidadPorcentual;

    /** Dividendos cobrados de este activo */
    private BigDecimal dividendosCobrados;

    /** Porcentaje del valor total de la cartera */
    private BigDecimal pesoEnCartera;
}
