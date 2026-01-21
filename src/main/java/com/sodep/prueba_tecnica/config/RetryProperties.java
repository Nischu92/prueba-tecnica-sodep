package com.sodep.prueba_tecnica.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "retry")
@Data
public class RetryProperties {
    
    private int maxAttempts = 3;
    private long delay = 1000;
}
