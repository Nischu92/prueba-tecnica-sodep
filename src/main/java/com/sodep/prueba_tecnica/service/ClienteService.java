package com.sodep.prueba_tecnica.service;

import com.sodep.prueba_tecnica.dto.ClienteRequestDTO;
import com.sodep.prueba_tecnica.dto.ClienteResponseDTO;
import com.sodep.prueba_tecnica.dto.ExternalUserDTO;
import com.sodep.prueba_tecnica.entity.Cliente;
import com.sodep.prueba_tecnica.exception.ClienteAlreadyExistsException;
import com.sodep.prueba_tecnica.exception.ClienteNotFoundException;
import com.sodep.prueba_tecnica.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ExternalApiService externalApiService;

    /**
     * Obtiene todos los clientes
     * 
     * @return lista de clientes
     */
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> getAll() {
        log.info("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por su ID
     * 
     * @param id el ID del cliente
     * @return el cliente encontrado
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO getById(Long id) {
        log.info("Buscando cliente con ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));
        return ClienteResponseDTO.fromEntity(cliente);
    }

    /**
     * Crea un nuevo cliente
     * 
     * @param request los datos del cliente
     * @return el cliente creado
     */
    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO request) {
        log.info("Creando nuevo cliente con email: {}", request.email());

        // Verificar si ya existe un cliente con ese email
        if (clienteRepository.existsByEmail(request.email())) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con el email: " + request.email());
        }

        // Crear y guardar el nuevo cliente
        Cliente cliente = new Cliente();
        updateClienteFromRequest(cliente, request);

        Cliente savedCliente = clienteRepository.save(cliente);

        // Resultado
        log.info("Cliente creado exitosamente con ID: {}", savedCliente.getId());
        return ClienteResponseDTO.fromEntity(savedCliente);
    }

    /**
     * Actualiza un cliente existente
     * 
     * @param id      el ID del cliente
     * @param request los nuevos datos
     * @return el cliente actualizado
     */
    @Transactional
    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO request) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));

        // Verificar email duplicado si se esta actualizando
        if (!cliente.getEmail().equals(request.email()) &&
                clienteRepository.existsByEmail(request.email())) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con el email: " + request.email());
        }

        // Actualizar campos
        updateClienteFromRequest(cliente, request);

        // Guardar cambios
        Cliente updatedCliente = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente");

        return ClienteResponseDTO.fromEntity(updatedCliente);
    }

    /**
     * Elimina un cliente
     * 
     * @param id el ID del cliente
     */
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando cliente con ID: {}", id);

        // Verificar si el cliente existe
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente no encontrado con ID: " + id);
        }

        // Eliminar cliente
        clienteRepository.deleteById(id);
        log.info("Cliente eliminado exitosamente");
    }

    /**
     * Sincroniza clientes desde la API externa
     * 
     * @return lista de clientes sincronizados
     */
    @Transactional
    public List<ClienteResponseDTO> syncFromExternalApi() {
        log.info("Iniciando sincronizacion de clientes desde API externa");

        // Obtener usuarios externos
        List<ExternalUserDTO> externalUsers = externalApiService.fetchUsers();

        // Procesar y guardar clientes
        List<Cliente> clientesSaved = externalUsers.stream()
                .map(externalUser -> {
                    return clienteRepository.findByIdExterno(String.valueOf(externalUser.id()))
                            .map(existingCliente -> {
                                // Actualizar cliente existente
                                log.debug("Actualizando cliente existente con ID externo: {}", externalUser.id());
                                updateClienteFromExternal(existingCliente, externalUser);
                                return clienteRepository.save(existingCliente);
                            })
                            .orElseGet(() -> {
                                // Crear nuevo cliente
                                log.debug("Creando nuevo cliente desde API externa: {}", externalUser.email());
                                ClienteRequestDTO request = externalUser.toClienteRequest();
                                Cliente newCliente = new Cliente();
                                updateClienteFromRequest(newCliente, request);
                                return clienteRepository.save(newCliente);
                            });
                })
                .collect(Collectors.toList());

        // Resultado
        log.info("Sincronizacion completada. {} clientes procesados", clientesSaved.size());

        return clientesSaved.stream()
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los campos de un cliente desde un ClienteRequestDTO
     * 
     * @param cliente el cliente a actualizar
     * @param request los datos del request
     */
    private void updateClienteFromRequest(Cliente cliente, ClienteRequestDTO request) {
        cliente.setNombre(request.nombre());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        cliente.setDireccion(request.direccion());
        cliente.setIdExterno(request.idExterno());
    }

    /**
     * Actualiza los datos de un cliente existente con datos externos
     * 
     * @param cliente      el cliente a actualizar
     * @param externalUser los datos externos
     */
    private void updateClienteFromExternal(Cliente cliente, ExternalUserDTO externalUser) {
        cliente.setNombre(externalUser.name());
        cliente.setEmail(externalUser.email());
        cliente.setTelefono(externalUser.phone());

        // Construir direccion completa (pattern matching)
        if (externalUser.address() instanceof ExternalUserDTO.Address addr) {
            String direccion = String.format("%s %s, %s %s",
                    addr.street(),
                    addr.suite(),
                    addr.city(),
                    addr.zipcode());
            cliente.setDireccion(direccion);
        }
        cliente.setIdExterno(String.valueOf(externalUser.id()));
    }
}
