package com.sodep.prueba_tecnica.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.external")
@Data
public class ExternalApiProperties {

    private String url;
    private String token;
}
