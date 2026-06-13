package com.simulador.cartera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo centralizado de errores.
 * Todos los errores devuelven un JSON consistente al frontend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado -> 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    // Ticker duplicado -> 409 Conflict
    @ExceptionHandler(TickerDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(TickerDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // Cantidad insuficiente para venta -> 400 Bad Request
    @ExceptionHandler(CantidadInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleCantidadInsuficiente(CantidadInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // Errores de validación de Bean Validation (@NotNull, @NotBlank...) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        String mensaje = "Error de validación: " + errores;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    // Cualquier otro error no controlado -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor: " + ex.getMessage()));
    }

    // Clase interna para la respuesta de error
    public record ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {
        public ErrorResponse(int status, String mensaje) {
            this(status, mensaje, LocalDateTime.now());
        }
    }
}
