package com.simulador.cartera.service;

import com.simulador.cartera.dto.ActivoRequestDTO;
import com.simulador.cartera.dto.ActivoResponseDTO;
import com.simulador.cartera.entity.Activo;
import com.simulador.cartera.exception.RecursoNoEncontradoException;
import com.simulador.cartera.exception.TickerDuplicadoException;
import com.simulador.cartera.repository.ActivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository activoRepository;

    public List<ActivoResponseDTO> listarTodos() {
        return activoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ActivoResponseDTO buscarPorId(Long id) {
        return activoRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Activo no encontrado con id: " + id));
    }

    public List<ActivoResponseDTO> buscar(String texto) {
        return activoRepository.buscarPorNombreOTicker(texto)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ActivoResponseDTO crear(ActivoRequestDTO dto) {
        // Verificar que el ticker no esté duplicado
        if (activoRepository.existsByTickerIgnoreCase(dto.getTicker())) {
            throw new TickerDuplicadoException("Ya existe un activo con el ticker: " + dto.getTicker());
        }

        Activo activo = Activo.builder()
                .ticker(dto.getTicker().toUpperCase())
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .sector(dto.getSector())
                .moneda(dto.getMoneda())
                .precioActual(dto.getPrecioActual())
                .build();

        return toResponseDTO(activoRepository.save(activo));
    }

    @Transactional
    public ActivoResponseDTO actualizar(Long id, ActivoRequestDTO dto) {
        Activo activo = activoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Activo no encontrado con id: " + id));

        // Si cambia el ticker, verificar que no esté duplicado
        if (!activo.getTicker().equalsIgnoreCase(dto.getTicker()) &&
                activoRepository.existsByTickerIgnoreCase(dto.getTicker())) {
            throw new TickerDuplicadoException("Ya existe un activo con el ticker: " + dto.getTicker());
        }

        activo.setTicker(dto.getTicker().toUpperCase());
        activo.setNombre(dto.getNombre());
        activo.setTipo(dto.getTipo());
        activo.setSector(dto.getSector());
        activo.setMoneda(dto.getMoneda());
        activo.setPrecioActual(dto.getPrecioActual());

        return toResponseDTO(activoRepository.save(activo));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!activoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Activo no encontrado con id: " + id);
        }
        activoRepository.deleteById(id);
    }

    // Método interno para convertir entidad a DTO
    public ActivoResponseDTO toResponseDTO(Activo activo) {
        return ActivoResponseDTO.builder()
                .id(activo.getId())
                .ticker(activo.getTicker())
                .nombre(activo.getNombre())
                .tipo(activo.getTipo())
                .sector(activo.getSector())
                .moneda(activo.getMoneda())
                .precioActual(activo.getPrecioActual())
                .build();
    }

    // Método para obtener la entidad directamente (usado por OperacionService)
    public Activo obtenerEntidad(Long id) {
        return activoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Activo no encontrado con id: " + id));
    }
}
