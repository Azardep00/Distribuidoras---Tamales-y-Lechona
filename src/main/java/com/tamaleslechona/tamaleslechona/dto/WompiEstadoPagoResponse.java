package com.tamaleslechona.tamaleslechona.dto;

public record WompiEstadoPagoResponse(
        Integer idPedido,
        String referencia,
        String estadoPago,
        String estadoPedido) {
}
