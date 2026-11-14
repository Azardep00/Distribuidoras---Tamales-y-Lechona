package com.tamaleslechona.tamaleslechona.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DetallePedidoRequest(
        @Schema(description = "Id (texto largo) de un producto, tomado de GET /api/productos",
                example = "6740f1c2a9b3d4e5f6a7b8c9") String idProducto,
        @Schema(example = "2") int cantidad) {}
