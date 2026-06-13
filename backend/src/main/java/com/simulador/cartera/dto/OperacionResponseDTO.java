package com.simulador.cartera.dto;

import com.simulador.cartera.enums.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para una operación.
 * Incluye datos básicos del activo para evitar llamadas extra al backend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionResponseDTO {

    private Long id;
    private Long activoId;
    private String activoTicker;
    private String activoNombre;
    private TipoOperacion tipoOperacion;
    private LocalDate fecha;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal importeTotal;
}
