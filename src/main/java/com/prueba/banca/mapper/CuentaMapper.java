package com.prueba.banca.mapper;

import com.prueba.banca.domain.Cuenta;
import com.prueba.banca.dto.CuentaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombre", source = "cliente.nombre")
    CuentaResponse toResponse(Cuenta cuenta);
}
