package com.simulador.cartera.controller;

import com.simulador.cartera.dto.OperacionRequestDTO;
import com.simulador.cartera.dto.OperacionResponseDTO;
import com.simulador.cartera.enums.TipoOperacion;
import com.simulador.cartera.service.OperacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;

    @GetMapping
    public ResponseEntity<List<OperacionResponseDTO>> listar(
            @RequestParam(required = false) Long activoId,
            @RequestParam(required = false) TipoOperacion tipo) {

        if (activoId != null && tipo != null) {
            return ResponseEntity.ok(operacionService.listarPorActivoYTipo(activoId, tipo));
        } else if (activoId != null) {
            return ResponseEntity.ok(operacionService.listarPorActivo(activoId));
        } else if (tipo != null) {
            return ResponseEntity.ok(operacionService.listarPorTipo(tipo));
        }
        return ResponseEntity.ok(operacionService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<OperacionResponseDTO> registrar(
            @Valid @RequestBody OperacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operacionService.registrar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        operacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
