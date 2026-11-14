package com.tamaleslechona.tamaleslechona.dto;

import com.tamaleslechona.tamaleslechona.model.EstadoPedido;

import io.swagger.v3.oas.annotations.media.Schema;

public record CambiarEstadoPedidoRequest(
        @Schema(description = "Solo el siguiente estado de la secuencia: PENDIENTE -> CONFIRMADO -> EN_PREPARACION -> ENTREGADO",
                example = "CONFIRMADO") EstadoPedido estado) {}
