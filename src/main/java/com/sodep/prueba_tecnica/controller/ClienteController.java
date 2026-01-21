package com.sodep.prueba_tecnica.controller;

import com.sodep.prueba_tecnica.dto.ClienteRequestDTO;
import com.sodep.prueba_tecnica.dto.ClienteResponseDTO;
import com.sodep.prueba_tecnica.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Obtiene todos los clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAll() {
        log.info("GET /api/clientes - Obteniendo todos los clientes");
        List<ClienteResponseDTO> clientes = clienteService.getAll();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtiene un cliente por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/clientes/{} - Obteniendo cliente", id);
        ClienteResponseDTO cliente = clienteService.getById(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Crea un nuevo cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO request) {
        log.info("POST /api/clientes - Creando nuevo cliente");
        ClienteResponseDTO cliente = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    /**
     * Actualiza un cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        log.info("PUT /api/clientes/{} - Actualizando cliente", id);
        ClienteResponseDTO cliente = clienteService.updateCliente(id, request);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Elimina un cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Eliminando cliente", id);
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Sincroniza clientes desde la API externa
     */
    @PostMapping("/sync")
    public ResponseEntity<List<ClienteResponseDTO>> sync() {
        log.info("POST /api/clientes/sync - Sincronizando clientes desde API externa");
        List<ClienteResponseDTO> clientes = clienteService.syncFromExternalApi();
        return ResponseEntity.ok(clientes);
    }
}
