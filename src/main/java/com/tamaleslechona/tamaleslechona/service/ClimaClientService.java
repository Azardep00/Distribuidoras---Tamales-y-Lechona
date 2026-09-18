package com.tamaleslechona.tamaleslechona.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.tamaleslechona.tamaleslechona.dto.ClimaResponse;

@Service
public class ClimaClientService {

    private final RestClient restClient;

    @Value("${microservicio.clima.url}")
    private String climaUrl;

    public ClimaClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ClimaResponse consultarClimaIbague() {
        // Coordenadas de Ibagué, Tolima
        String uri = climaUrl + "?latitude=4.44&longitude=-75.24&current_weather=true";

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(ClimaResponse.class);
    }
}