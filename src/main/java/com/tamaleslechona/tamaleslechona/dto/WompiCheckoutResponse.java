package com.tamaleslechona.tamaleslechona.dto;

public record WompiCheckoutResponse(
        String idPedido,
        String referencia,
        long montoEnCentavos,
        String moneda,
        String firmaIntegridad) {
}
