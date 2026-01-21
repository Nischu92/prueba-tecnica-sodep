package com.sodep.prueba_tecnica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio") String nombre,

        @NotBlank(message = "El email es obligatorio") @Email(message = "El email debe ser valido") String email,

        String telefono,

        String direccion,

        String idExterno) {
}
