package com.simulador.cartera.dto;

import com.simulador.cartera.enums.TipoActivo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear o actualizar un activo.
 * Separa la capa de API de la entidad JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivoRequestDTO {

    @NotBlank(message = "El ticker es obligatorio")
    private String ticker;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de activo es obligatorio")
    private TipoActivo tipo;

    private String sector;

    private String moneda;

    @NotNull(message = "El precio actual es obligatorio")
    @DecimalMin(value = "0.0001", message = "El precio debe ser mayor que cero")
    private BigDecimal precioActual;
}
