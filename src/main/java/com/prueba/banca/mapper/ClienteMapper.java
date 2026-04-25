package com.prueba.banca.mapper;

import com.prueba.banca.domain.Cliente;
import com.prueba.banca.dto.ClienteRequest;
import com.prueba.banca.dto.ClienteResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentas", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentas", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    void updateEntity(ClienteRequest request, @MappingTarget Cliente cliente);
}
