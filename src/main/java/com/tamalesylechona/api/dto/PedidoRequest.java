package main.java.com.tamalesylechona.api.dto;

import java.util.List;

public record PedidoRequest(Integer idCliente, List<DetallePedidoRequest> detalles) {}