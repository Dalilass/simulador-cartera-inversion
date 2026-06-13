package com.simulador.cartera.service;

import com.simulador.cartera.dto.PosicionDTO;
import com.simulador.cartera.dto.PuntoEvolucionDTO;
import com.simulador.cartera.dto.ResumenCarteraDTO;
import com.simulador.cartera.entity.Activo;
import com.simulador.cartera.entity.Operacion;
import com.simulador.cartera.enums.TipoOperacion;
import com.simulador.cartera.repository.ActivoRepository;
import com.simulador.cartera.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CarteraService {

    private final ActivoRepository activoRepository;
    private final OperacionRepository operacionRepository;

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final BigDecimal CIEN = new BigDecimal("100");

    /**
     * Calcula el resumen completo de la cartera para el dashboard.
     */
    public ResumenCarteraDTO calcularResumen() {
        List<Activo> activos = activoRepository.findAll();
        List<Operacion> todasOperaciones = operacionRepository.findAll();

        // Agrupar operaciones por activo
        Map<Long, List<Operacion>> operacionesPorActivo = new HashMap<>();
        for (Operacion op : todasOperaciones) {
            operacionesPorActivo
                .computeIfAbsent(op.getActivo().getId(), k -> new ArrayList<>())
                .add(op);
        }

        List<PosicionDTO> posiciones = new ArrayList<>();
        BigDecimal valorTotalCartera = BigDecimal.ZERO;
        BigDecimal totalInvertido = BigDecimal.ZERO;
        BigDecimal dividendosTotales = BigDecimal.ZERO;

        // Calcular posición por cada activo
        for (Activo activo : activos) {
            List<Operacion> ops = operacionesPorActivo.getOrDefault(activo.getId(), Collections.emptyList());
            PosicionDTO posicion = calcularPosicion(activo, ops);

            // Solo incluir activos con cantidad > 0
            if (posicion.getCantidadActual().compareTo(BigDecimal.ZERO) > 0) {
                posiciones.add(posicion);
                valorTotalCartera = valorTotalCartera.add(posicion.getValorActual());
                totalInvertido = totalInvertido.add(posicion.getCosteTotal());
                dividendosTotales = dividendosTotales.add(posicion.getDividendosCobrados());
            } else if (posicion.getDividendosCobrados().compareTo(BigDecimal.ZERO) > 0) {
                // Activo ya liquidado pero con dividendos cobrados
                dividendosTotales = dividendosTotales.add(posicion.getDividendosCobrados());
            }
        }

        // Calcular pesos de cada posición sobre el total de la cartera
        if (valorTotalCartera.compareTo(BigDecimal.ZERO) > 0) {
            for (PosicionDTO pos : posiciones) {
                BigDecimal peso = pos.getValorActual()
                    .multiply(CIEN)
                    .divide(valorTotalCartera, 2, RoundingMode.HALF_UP);
                pos.setPesoEnCartera(peso);
            }
        }

        BigDecimal beneficioPerdidaTotal = valorTotalCartera
            .subtract(totalInvertido)
            .add(dividendosTotales);

        BigDecimal rentabilidadTotal = BigDecimal.ZERO;
        if (totalInvertido.compareTo(BigDecimal.ZERO) > 0) {
            rentabilidadTotal = beneficioPerdidaTotal
                .multiply(CIEN)
                .divide(totalInvertido, 2, RoundingMode.HALF_UP);
        }

        // Distribución por tipo de activo
        Map<String, BigDecimal> distribucionPorTipo = calcularDistribucionPorTipo(posiciones, valorTotalCartera);

        return ResumenCarteraDTO.builder()
                .valorTotalCartera(valorTotalCartera.setScale(2, RoundingMode.HALF_UP))
                .totalInvertido(totalInvertido.setScale(2, RoundingMode.HALF_UP))
                .beneficioPerdidaTotal(beneficioPerdidaTotal.setScale(2, RoundingMode.HALF_UP))
                .rentabilidadTotal(rentabilidadTotal)
                .dividendosTotales(dividendosTotales.setScale(2, RoundingMode.HALF_UP))
                .numeroActivos(posiciones.size())
                .distribucionPorTipo(distribucionPorTipo)
                .posiciones(posiciones)
                .evolucion(generarEvolucionSimulada(todasOperaciones))
                .build();
    }

    /**
     * Calcula la posición actual de un activo basándose en sus operaciones.
     */
    private PosicionDTO calcularPosicion(Activo activo, List<Operacion> operaciones) {
        BigDecimal cantidadComprada = BigDecimal.ZERO;
        BigDecimal costeCompras = BigDecimal.ZERO;
        BigDecimal cantidadVendida = BigDecimal.ZERO;
        BigDecimal dividendosCobrados = BigDecimal.ZERO;

        for (Operacion op : operaciones) {
            switch (op.getTipoOperacion()) {
                case COMPRA -> {
                    cantidadComprada = cantidadComprada.add(op.getCantidad());
                    costeCompras = costeCompras.add(op.getImporteTotal());
                }
                case VENTA -> {
                    cantidadVendida = cantidadVendida.add(op.getCantidad());
                }
                case DIVIDENDO -> {
                    dividendosCobrados = dividendosCobrados.add(op.getImporteTotal());
                }
            }
        }

        BigDecimal cantidadActual = cantidadComprada.subtract(cantidadVendida);

        // Precio medio de compra ponderado
        BigDecimal precioMedioCompra = BigDecimal.ZERO;
        if (cantidadComprada.compareTo(BigDecimal.ZERO) > 0) {
            precioMedioCompra = costeCompras.divide(cantidadComprada, 4, RoundingMode.HALF_UP);
        }

        // Coste actual proporcional a las unidades que quedan
        BigDecimal costeTotal = cantidadActual.multiply(precioMedioCompra);
        BigDecimal valorActual = cantidadActual.multiply(activo.getPrecioActual());
        BigDecimal beneficioPerdida = valorActual.subtract(costeTotal);

        BigDecimal rentabilidadPorcentual = BigDecimal.ZERO;
        if (costeTotal.compareTo(BigDecimal.ZERO) > 0) {
            rentabilidadPorcentual = beneficioPerdida
                .multiply(CIEN)
                .divide(costeTotal, 2, RoundingMode.HALF_UP);
        }

        return PosicionDTO.builder()
                .activoId(activo.getId())
                .ticker(activo.getTicker())
                .nombre(activo.getNombre())
                .tipo(activo.getTipo())
                .sector(activo.getSector())
                .moneda(activo.getMoneda())
                .cantidadActual(cantidadActual.setScale(4, RoundingMode.HALF_UP))
                .precioMedioCompra(precioMedioCompra)
                .precioActual(activo.getPrecioActual())
                .valorActual(valorActual.setScale(2, RoundingMode.HALF_UP))
                .costeTotal(costeTotal.setScale(2, RoundingMode.HALF_UP))
                .beneficioPerdida(beneficioPerdida.setScale(2, RoundingMode.HALF_UP))
                .rentabilidadPorcentual(rentabilidadPorcentual)
                .dividendosCobrados(dividendosCobrados.setScale(2, RoundingMode.HALF_UP))
                .pesoEnCartera(BigDecimal.ZERO) // se calcula después con el total
                .build();
    }

    /**
     * Calcula el porcentaje que representa cada tipo de activo sobre el total de la cartera.
     */
    private Map<String, BigDecimal> calcularDistribucionPorTipo(
            List<PosicionDTO> posiciones, BigDecimal valorTotal) {

        Map<String, BigDecimal> distribucion = new LinkedHashMap<>();
        if (valorTotal.compareTo(BigDecimal.ZERO) == 0) return distribucion;

        Map<String, BigDecimal> valorPorTipo = new LinkedHashMap<>();
        for (PosicionDTO pos : posiciones) {
            String tipo = pos.getTipo().name();
            valorPorTipo.merge(tipo, pos.getValorActual(), BigDecimal::add);
        }

        for (Map.Entry<String, BigDecimal> entry : valorPorTipo.entrySet()) {
            BigDecimal porcentaje = entry.getValue()
                .multiply(CIEN)
                .divide(valorTotal, 2, RoundingMode.HALF_UP);
            distribucion.put(entry.getKey(), porcentaje);
        }

        return distribucion;
    }

    /**
     * Genera puntos de evolución simulados basados en las fechas de las operaciones.
     * Calcula el valor de la cartera mes a mes desde la primera operación.
     */
    private List<PuntoEvolucionDTO> generarEvolucionSimulada(List<Operacion> operaciones) {
        if (operaciones.isEmpty()) return Collections.emptyList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<PuntoEvolucionDTO> puntos = new ArrayList<>();

        // Encontrar el mes de la primera operación
        LocalDate primeraFecha = operaciones.stream()
            .map(Operacion::getFecha)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now().minusMonths(12));

        LocalDate mesActual = primeraFecha.withDayOfMonth(1);
        LocalDate ahora = LocalDate.now().withDayOfMonth(1);

        // Para cada mes, calcular el valor aproximado de la cartera
        while (!mesActual.isAfter(ahora)) {
            final LocalDate mesRef = mesActual;
            BigDecimal valorMes = BigDecimal.ZERO;

            // Para cada activo, calcular cuántas unidades tenía en ese mes
            // y multiplicar por el precio actual (simplificación educativa)
            Map<Long, BigDecimal> cantidadesPorActivo = new HashMap<>();
            Map<Long, BigDecimal> preciosPorActivo = new HashMap<>();

            for (Operacion op : operaciones) {
                if (!op.getFecha().isAfter(mesRef.plusMonths(1).minusDays(1))) {
                    Long activoId = op.getActivo().getId();
                    preciosPorActivo.put(activoId, op.getActivo().getPrecioActual());
                    BigDecimal actual = cantidadesPorActivo.getOrDefault(activoId, BigDecimal.ZERO);
                    if (op.getTipoOperacion() == TipoOperacion.COMPRA) {
                        cantidadesPorActivo.put(activoId, actual.add(op.getCantidad()));
                    } else if (op.getTipoOperacion() == TipoOperacion.VENTA) {
                        cantidadesPorActivo.put(activoId, actual.subtract(op.getCantidad()));
                    }
                }
            }

            for (Map.Entry<Long, BigDecimal> entry : cantidadesPorActivo.entrySet()) {
                BigDecimal cantidad = entry.getValue();
                BigDecimal precio = preciosPorActivo.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                if (cantidad.compareTo(BigDecimal.ZERO) > 0) {
                    valorMes = valorMes.add(cantidad.multiply(precio));
                }
            }

            puntos.add(PuntoEvolucionDTO.builder()
                .mes(mesRef.format(fmt))
                .valor(valorMes.setScale(2, RoundingMode.HALF_UP))
                .build());

            mesActual = mesActual.plusMonths(1);
        }

        return puntos;
    }
}
