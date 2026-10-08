package com.tamaleslechona.tamaleslechona.dto;

public record WompiCheckoutResponse(
        Integer idPedido,
        String referencia,
        long montoEnCentavos,
        String moneda,
        String firmaIntegridad) {
}
