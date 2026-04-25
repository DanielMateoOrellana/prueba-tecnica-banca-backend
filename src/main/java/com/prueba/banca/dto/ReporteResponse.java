package com.prueba.banca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteResponse(
        String clienteId,
        String cliente,
        LocalDate desde,
        LocalDate hasta,
        List<ReporteEntry> movimientos,
        BigDecimal totalDebitos,
        BigDecimal totalCreditos
) {}
