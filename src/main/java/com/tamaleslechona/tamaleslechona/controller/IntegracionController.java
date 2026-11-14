package com.tamaleslechona.tamaleslechona.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tamaleslechona.tamaleslechona.client.ClimaClient;

@Tag(name = "Integración", description = "Consulta de servicios externos.")
@RestController
@RequestMapping("/api/integracion")
public class IntegracionController {

    private final ClimaClient climaClient;

    public IntegracionController(ClimaClient climaClient) {
        this.climaClient = climaClient;
    }

    @Operation(summary = "Consultar el clima (servicio externo Open-Meteo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @SecurityRequirements
    @GetMapping("/clima")
    public ResponseEntity<?> clima() {
        try {
            return ResponseEntity.ok(climaClient.consultarClima());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("mensaje", "Servicio de clima no disponible"));
        }
    }
}