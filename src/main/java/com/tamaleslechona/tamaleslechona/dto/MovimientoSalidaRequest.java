package com.tamaleslechona.tamaleslechona.dto;

// Se usa tanto para salidas como para reversiones (misma forma: producto + cantidad + motivo).
public record MovimientoSalidaRequest(Integer idProducto, int cantidad, String motivo) {}
