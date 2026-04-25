package com.prueba.banca.mapper;

import com.prueba.banca.domain.Movimiento;
import com.prueba.banca.dto.MovimientoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(target = "numeroCuenta", source = "cuenta.numeroCuenta")
    MovimientoResponse toResponse(Movimiento movimiento);
}
