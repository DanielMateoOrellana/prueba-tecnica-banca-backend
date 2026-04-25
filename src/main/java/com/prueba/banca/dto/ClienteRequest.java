package com.prueba.banca.dto;

import com.prueba.banca.domain.Genero;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @NotNull(message = "El genero es obligatorio")
        Genero genero,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 0, message = "La edad debe ser mayor o igual a 0")
        Integer edad,

        @NotBlank(message = "La identificacion es obligatoria")
        @Size(max = 30)
        String identificacion,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 200)
        String direccion,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "^[+0-9\\-\\s]{6,30}$", message = "Telefono invalido")
        String telefono,

        @NotBlank(message = "El clienteId es obligatorio")
        @Size(max = 30)
        String clienteId,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 4, max = 100, message = "La contrasena debe tener entre 4 y 100 caracteres")
        String contrasena,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {}
