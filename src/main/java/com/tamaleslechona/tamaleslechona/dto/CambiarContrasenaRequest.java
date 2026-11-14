package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CambiarContrasenaRequest(
        @Schema(example = "clave123") String contrasenaActual,
        @Schema(description = "Mínimo 6 caracteres", example = "nuevaClave456") String contrasenaNueva) {}
