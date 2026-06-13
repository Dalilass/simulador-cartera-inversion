package com.simulador.cartera.controller;

import com.simulador.cartera.dto.ActivoRequestDTO;
import com.simulador.cartera.dto.ActivoResponseDTO;
import com.simulador.cartera.service.ActivoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService activoService;

    @GetMapping
    public ResponseEntity<List<ActivoResponseDTO>> listar(
            @RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            return ResponseEntity.ok(activoService.buscar(buscar));
        }
        return ResponseEntity.ok(activoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(activoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ActivoResponseDTO> crear(@Valid @RequestBody ActivoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActivoRequestDTO dto) {
        return ResponseEntity.ok(activoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        activoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
