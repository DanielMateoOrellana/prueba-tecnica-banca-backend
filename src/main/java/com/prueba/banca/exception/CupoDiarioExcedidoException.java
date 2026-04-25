package com.prueba.banca.exception;

public class CupoDiarioExcedidoException extends BancaException {
    public CupoDiarioExcedidoException() {
        super("Cupo diario Excedido");
    }
}
