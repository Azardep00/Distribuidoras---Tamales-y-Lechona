package com.tamaleslechona.tamaleslechona.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tamaleslechona.tamaleslechona.client.ClimaClient;

@RestController
@RequestMapping("/api/integracion")
public class IntegracionController {

    private final ClimaClient climaClient;

    public IntegracionController(ClimaClient climaClient) {
        this.climaClient = climaClient;
    }

    @GetMapping("/clima")
    public ResponseEntity<?> clima() {
        try {
            return ResponseEntity.ok(climaClient.consultarClima());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Servicio de clima no disponible");
        }
    }
}