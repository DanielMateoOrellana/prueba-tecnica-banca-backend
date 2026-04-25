package com.prueba.banca.dto;

import com.prueba.banca.domain.TipoCuenta;
import com.prueba.banca.domain.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReporteEntry(
        LocalDateTime fecha,
        String cliente,
        String numeroCuenta,
        TipoCuenta tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        TipoMovimiento tipoMovimiento,
        BigDecimal saldoDisponible
) {}
