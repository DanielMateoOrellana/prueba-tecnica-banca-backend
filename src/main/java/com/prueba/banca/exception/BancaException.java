package com.prueba.banca.exception;

public abstract class BancaException extends RuntimeException {
    protected BancaException(String message) {
        super(message);
    }
}
