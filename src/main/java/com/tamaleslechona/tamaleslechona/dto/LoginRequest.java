package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "carlos@example.com") String correo,
        @Schema(example = "clave123") String contrasena) {}
