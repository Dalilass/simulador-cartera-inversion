package com.simulador.cartera.service;

import com.simulador.cartera.dto.OperacionRequestDTO;
import com.simulador.cartera.dto.OperacionResponseDTO;
import com.simulador.cartera.entity.Activo;
import com.simulador.cartera.entity.Operacion;
import com.simulador.cartera.enums.TipoOperacion;
import com.simulador.cartera.exception.CantidadInsuficienteException;
import com.simulador.cartera.exception.RecursoNoEncontradoException;
import com.simulador.cartera.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final ActivoService activoService;

    public List<OperacionResponseDTO> listarTodas() {
        return operacionRepository.findAllConActivo()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<OperacionResponseDTO> listarPorActivo(Long activoId) {
        return operacionRepository.findByActivoIdConActivo(activoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<OperacionResponseDTO> listarPorTipo(TipoOperacion tipo) {
        return operacionRepository.findByTipoOperacion(tipo)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<OperacionResponseDTO> listarPorActivoYTipo(Long activoId, TipoOperacion tipo) {
        return operacionRepository.findByActivoIdAndTipoOperacion(activoId, tipo)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public OperacionResponseDTO registrar(OperacionRequestDTO dto) {
        Activo activo = activoService.obtenerEntidad(dto.getActivoId());

        // Si es una venta, verificar que haya suficiente cantidad disponible
        if (dto.getTipoOperacion() == TipoOperacion.VENTA) {
            BigDecimal cantidadDisponible = calcularCantidadDisponible(dto.getActivoId());
            if (dto.getCantidad().compareTo(cantidadDisponible) > 0) {
                throw new CantidadInsuficienteException(
                    String.format("No puedes vender %.4f unidades. Solo tienes %.4f disponibles de %s.",
                        dto.getCantidad(), cantidadDisponible, activo.getTicker())
                );
            }
        }

        Operacion operacion = Operacion.builder()
                .activo(activo)
                .tipoOperacion(dto.getTipoOperacion())
                .fecha(dto.getFecha())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .importeTotal(dto.getCantidad().multiply(dto.getPrecioUnitario()))
                .build();

        return toResponseDTO(operacionRepository.save(operacion));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!operacionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Operación no encontrada con id: " + id);
        }
        operacionRepository.deleteById(id);
    }

    /**
     * Calcula cuántas unidades tiene disponibles el usuario de un activo.
     * Suma compras, resta ventas. Los dividendos no afectan la cantidad.
     */
    public BigDecimal calcularCantidadDisponible(Long activoId) {
        List<Operacion> operaciones = operacionRepository.findByActivoId(activoId);
        BigDecimal total = BigDecimal.ZERO;

        for (Operacion op : operaciones) {
            if (op.getTipoOperacion() == TipoOperacion.COMPRA) {
                total = total.add(op.getCantidad());
            } else if (op.getTipoOperacion() == TipoOperacion.VENTA) {
                total = total.subtract(op.getCantidad());
            }
            // DIVIDENDO no afecta la cantidad de activos
        }
        return total;
    }

    // Convierte entidad a DTO de respuesta
    public OperacionResponseDTO toResponseDTO(Operacion operacion) {
        return OperacionResponseDTO.builder()
                .id(operacion.getId())
                .activoId(operacion.getActivo().getId())
                .activoTicker(operacion.getActivo().getTicker())
                .activoNombre(operacion.getActivo().getNombre())
                .tipoOperacion(operacion.getTipoOperacion())
                .fecha(operacion.getFecha())
                .cantidad(operacion.getCantidad())
                .precioUnitario(operacion.getPrecioUnitario())
                .importeTotal(operacion.getImporteTotal())
                .build();
    }
}
