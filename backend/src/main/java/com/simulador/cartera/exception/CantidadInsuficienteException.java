package com.simulador.cartera.exception;

public class CantidadInsuficienteException extends RuntimeException {
    public CantidadInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
