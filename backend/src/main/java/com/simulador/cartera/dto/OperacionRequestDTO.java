package com.simulador.cartera.dto;

import com.simulador.cartera.enums.TipoOperacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para registrar una nueva operación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionRequestDTO {

    @NotNull(message = "El ID del activo es obligatorio")
    private Long activoId;

    @NotNull(message = "El tipo de operación es obligatorio")
    private TipoOperacion tipoOperacion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor que cero")
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0001", message = "El precio unitario debe ser mayor que cero")
    private BigDecimal precioUnitario;
}
