package com.sodep.prueba_tecnica.dto;

import com.sodep.prueba_tecnica.entity.Cliente;

import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        String idExterno,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {

    // Convierte entidad a DTO
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getIdExterno(),
                cliente.getFechaCreacion(),
                cliente.getFechaActualizacion());
    }
}
