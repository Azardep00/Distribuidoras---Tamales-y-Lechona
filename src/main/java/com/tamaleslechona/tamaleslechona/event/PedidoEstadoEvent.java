package com.tamaleslechona.tamaleslechona.event;

import java.time.Instant;

/**
 * Hecho ocurrido: un pedido cambio de estado.
 *
 * Es un record (inmutable) porque un evento describe algo que YA paso:
 * no se modifica despues. Por eso NO es una entidad de base de datos.
 */
public record PedidoEstadoEvent(
        String idPedido,
        String correoCliente,
        String estadoNuevo,
        Instant ocurridoEn) {
}
