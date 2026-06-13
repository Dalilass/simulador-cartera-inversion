package com.simulador.cartera.controller;

import com.simulador.cartera.dto.ResumenCarteraDTO;
import com.simulador.cartera.service.CarteraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cartera")
@RequiredArgsConstructor
public class CarteraController {

    private final CarteraService carteraService;

    /**
     * Devuelve el resumen completo de la cartera:
     * valor total, invertido, beneficio, posiciones y datos para gráficas.
     */
    @GetMapping("/resumen")
    public ResponseEntity<ResumenCarteraDTO> resumen() {
        return ResponseEntity.ok(carteraService.calcularResumen());
    }
}
