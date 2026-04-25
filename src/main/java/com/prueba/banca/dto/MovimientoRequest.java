package com.prueba.banca.dto;

import com.prueba.banca.domain.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record MovimientoRequest(
        @NotBlank(message = "El numero de cuenta es obligatorio")
        String numeroCuenta,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,

        @NotNull(message = "El valor es obligatorio")
        @Positive(message = "El valor debe ser positivo (ingrese monto absoluto)")
        BigDecimal valor
) {}
