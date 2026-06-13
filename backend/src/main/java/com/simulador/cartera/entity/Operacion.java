package com.simulador.cartera.entity;

import com.simulador.cartera.enums.TipoOperacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "operaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El activo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;

    @NotNull(message = "El tipo de operación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOperacion tipoOperacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor que cero")
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que cero")
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal precioUnitario;

    // Calculado: cantidad * precioUnitario
    // En DIVIDENDO representa el importe total del dividendo recibido
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal importeTotal;

    @PrePersist
    @PreUpdate
    private void calcularImporteTotal() {
        if (cantidad != null && precioUnitario != null) {
            this.importeTotal = cantidad.multiply(precioUnitario);
        }
    }
}
