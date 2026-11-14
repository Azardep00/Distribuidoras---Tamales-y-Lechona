package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Se usa tanto para salidas como para reversiones (misma forma: producto + cantidad + motivo).
public record MovimientoSalidaRequest(
        @Schema(example = "6740f1c2a9b3d4e5f6a7b8c9") String idProducto,
        @Schema(example = "3") int cantidad,
        @Schema(example = "Producto dañado") String motivo) {}
