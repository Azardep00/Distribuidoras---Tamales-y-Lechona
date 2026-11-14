package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MovimientoEntradaRequest(
        @Schema(example = "6740f1c2a9b3d4e5f6a7b8c9") String idProducto,
        @Schema(example = "6740f1c2a9b3d4e5f6a7b8bb") String idProveedor,
        @Schema(example = "10") int cantidad,
        @Schema(example = "Compra semanal") String motivo) {}
