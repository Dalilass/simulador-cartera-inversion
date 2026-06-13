package com.simulador.cartera.exception;

public class TickerDuplicadoException extends RuntimeException {
    public TickerDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
