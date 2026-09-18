package com.tamaleslechona.tamaleslechona.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tamaleslechona.tamaleslechona.dto.ClimaResponse;
import com.tamaleslechona.tamaleslechona.service.ClimaClientService;

@RestController
@RequestMapping("/api/integracion")
public class IntegracionController {

    private final ClimaClientService climaClient;

    public IntegracionController(ClimaClientService climaClient) {
        this.climaClient = climaClient;
    }

    @GetMapping("/clima")
    public ClimaResponse clima() {
        return climaClient.consultarClimaIbague();
    }
}