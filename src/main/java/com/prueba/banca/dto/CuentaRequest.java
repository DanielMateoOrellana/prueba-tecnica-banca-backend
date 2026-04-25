package com.prueba.banca.dto;

import com.prueba.banca.domain.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CuentaRequest(
        @NotBlank(message = "El numero de cuenta es obligatorio")
        @Size(max = 30)
        String numeroCuenta,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo")
        BigDecimal saldoInicial,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado,

        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId
) {}
