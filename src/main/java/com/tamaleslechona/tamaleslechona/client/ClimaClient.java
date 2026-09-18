package com.tamaleslechona.tamaleslechona.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.tamaleslechona.tamaleslechona.dto.ClimaResponse;

@Component
public class ClimaClient {

    private final RestClient restClient;

    public ClimaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ClimaResponse consultarClima() {
        return restClient.get()
                .uri("/v1/forecast?latitude=4.44&longitude=-75.24&current_weather=true")
                .retrieve()
                .body(ClimaResponse.class);
    }
}