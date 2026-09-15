package tamaleslechona.dto;

import java.util.List;

public record PedidoRequest(Integer idCliente, List<DetallePedidoRequest> detalles) {}