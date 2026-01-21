package com.sodep.prueba_tecnica.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@RequiredArgsConstructor
public class RetryConfig {

    private final RetryProperties retryProperties;

    public int getMaxAttempts() {
        return retryProperties.getMaxAttempts();
    }

    public long getDelay() {
        return retryProperties.getDelay();
    }
}
