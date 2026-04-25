package com.prueba.banca.dto;

import com.prueba.banca.domain.TipoCuenta;
import java.math.BigDecimal;

public record CuentaResponse(
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoActual,
        Boolean estado,
        String clienteId,
        String clienteNombre
) {}
