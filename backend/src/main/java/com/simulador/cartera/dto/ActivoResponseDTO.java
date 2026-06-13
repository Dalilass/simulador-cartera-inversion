package com.simulador.cartera.dto;

import com.simulador.cartera.enums.TipoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para un activo.
 * Lo que el frontend recibe al pedir activos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoResponseDTO {

    private Long id;
    private String ticker;
    private String nombre;
    private TipoActivo tipo;
    private String sector;
    private String moneda;
    private BigDecimal precioActual;
}
