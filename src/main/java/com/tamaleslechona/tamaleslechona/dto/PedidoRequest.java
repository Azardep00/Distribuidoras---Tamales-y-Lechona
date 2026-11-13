package com.tamaleslechona.tamaleslechona.dto;

import java.util.List;

public record PedidoRequest(String idCliente, List<DetallePedidoRequest> detalles) {}
