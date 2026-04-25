package com.prueba.banca.dto;

import com.prueba.banca.domain.Genero;

public record ClienteResponse(
        Long id,
        String nombre,
        Genero genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        String clienteId,
        Boolean estado
) {}
