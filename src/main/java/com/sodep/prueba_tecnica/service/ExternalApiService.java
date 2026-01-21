package com.sodep.prueba_tecnica.service;

import com.sodep.prueba_tecnica.dto.ExternalUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {

    private final RestClient restClient;

    @Value("${api.external.token:}")
    private String apiToken;

    /**
     * Obtiene todos los usuarios de la API externa
     * 
     * @return lista de usuarios externos
     */
    @Retryable(retryFor = { RestClientException.class,
            Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<ExternalUserDTO> fetchUsers() {
        log.info("Intentando obtener usuarios de la API externa");

        try {
            // Obtener la lista de usuarios de la API externa
            RestClient.RequestHeadersSpec<?> request = restClient.get().uri("/users");
            if (apiToken != null && !apiToken.isBlank()) {
                request = request.header("Authorization", "Bearer " + apiToken);
            }
            List<ExternalUserDTO> users = request.retrieve().body(new ParameterizedTypeReference<>() {
            });

            // Resultado
            log.info("Se obtuvieron {} usuarios exitosamente", users != null ? users.size() : 0);
            return users;

        } catch (Exception e) {
            // Error
            log.error("Error al obtener usuarios de la API externa: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un usuario especifico por ID de la API externa
     * 
     * @param userId el ID del usuario
     * @return el usuario externo
     */
    @Retryable(retryFor = { RestClientException.class,
            Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ExternalUserDTO fetchUserById(Long userId) {
        log.info("Intentando obtener usuario con ID: {}", userId);

        try {
            // Obtener el usuario de la API externa
            ExternalUserDTO user = restClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .body(ExternalUserDTO.class);

            // Resultado
            log.info("Usuario obtenido exitosamente: {}", user != null ? user.email() : "null");
            return user;

        } catch (Exception e) {
            // Error
            log.error("Error al obtener usuario {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}
