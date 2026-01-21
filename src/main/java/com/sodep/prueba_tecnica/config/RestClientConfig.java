package com.sodep.prueba_tecnica.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ExternalApiProperties apiProperties;

    @Bean
    public RestClient restClient() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(apiProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // Agregar el token si esta disponible
        String token = apiProperties.getToken();
        if (token != null && !token.isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        return builder.build();
    }
}
