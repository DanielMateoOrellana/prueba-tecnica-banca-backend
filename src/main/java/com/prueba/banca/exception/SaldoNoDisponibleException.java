package com.prueba.banca.exception;

public class SaldoNoDisponibleException extends BancaException {
    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }

    public SaldoNoDisponibleException(String message) {
        super(message);
    }
}
