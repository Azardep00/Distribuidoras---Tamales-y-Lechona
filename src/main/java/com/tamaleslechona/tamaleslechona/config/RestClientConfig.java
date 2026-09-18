package com.tamaleslechona.tamaleslechona.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${microservicio.clima.url}")
    private String climaUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(climaUrl)
                .build();
    }
}