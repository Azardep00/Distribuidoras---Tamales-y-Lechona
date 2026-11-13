package com.tamaleslechona.tamaleslechona.dto;

public record WompiEstadoPagoResponse(
        String idPedido,
        String referencia,
        String estadoPago,
        String estadoPedido) {
}
