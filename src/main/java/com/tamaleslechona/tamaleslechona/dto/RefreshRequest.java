package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshRequest(
        @Schema(description = "El campo refreshToken devuelto por el login", example = "pega-aqui-el-refresh-token")
        String refreshToken) {}
