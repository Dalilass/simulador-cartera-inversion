package com.simulador.cartera.entity;

import com.simulador.cartera.enums.TipoActivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El ticker es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String ticker;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El tipo de activo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActivo tipo;

    @Column
    private String sector;

    @Column(length = 10)
    private String moneda;

    @NotNull(message = "El precio actual es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal precioActual;

    // Relación con operaciones - se carga de forma lazy para mejor rendimiento
    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Operacion> operaciones = new ArrayList<>();
}
