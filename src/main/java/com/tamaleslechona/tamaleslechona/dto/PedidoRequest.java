package com.tamaleslechona.tamaleslechona.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record PedidoRequest(
        @Schema(description = "Solo lo usan los empleados; un cliente siempre usa el id de su token",
                example = "6740f1c2a9b3d4e5f6a7b8aa") String idCliente,
        List<DetallePedidoRequest> detalles) {}
