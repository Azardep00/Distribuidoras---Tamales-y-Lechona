package main.java.com.tamalesylechona.api.model;

// Secuencia normal: PENDIENTE -> CONFIRMADO -> EN_PREPARACION -> ENTREGADO.
// CANCELADO es una salida especial, alcanzable desde cualquier estado previo
// a ENTREGADO (ver PedidoService.cancelar).
public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    EN_PREPARACION,
    ENTREGADO,
    CANCELADO
}
