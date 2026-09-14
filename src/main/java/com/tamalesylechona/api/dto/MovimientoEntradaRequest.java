package main.java.com.tamalesylechona.api.dto;

public record MovimientoEntradaRequest(Integer idProducto, Integer idProveedor, int cantidad, String motivo) {}
