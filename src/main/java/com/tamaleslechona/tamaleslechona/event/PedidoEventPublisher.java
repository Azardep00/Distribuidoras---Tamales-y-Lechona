package com.tamaleslechona.tamaleslechona.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica los eventos de pedido en Kafka.
 *
 * Solo se usa cuando {@code notificaciones.kafka.habilitado=true}. Por
 * defecto el sistema sigue notificando por REST, para no obligar a nadie
 * del equipo a levantar un broker para trabajar.
 */
@Component
public class PedidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventPublisher.class);

    private final KafkaTemplate<Integer, PedidoEstadoEvent> kafkaTemplate;
    private final String topico;

    public PedidoEventPublisher(
            KafkaTemplate<Integer, PedidoEstadoEvent> kafkaTemplate,
            @Value("${notificaciones.kafka.topico:pedidos.estado-cambiado}") String topico) {
        this.kafkaTemplate = kafkaTemplate;
        this.topico = topico;
    }

    public void publicar(PedidoEstadoEvent evento) {
        // La clave es el id del pedido: Kafka manda todos los eventos de un
        // mismo pedido a la misma particion, y asi llegan EN ORDEN.
        kafkaTemplate.send(topico, evento.idPedido(), evento)
                .whenComplete((resultado, error) -> {
                    if (error != null) {
                        // Es asincrono: si el broker esta caido, no rompe el
                        // cambio de pedido, solo se registra el aviso.
                        log.warn("No se pudo publicar el evento del pedido #{}: {}",
                                evento.idPedido(), error.getMessage());
                    } else {
                        log.info("Evento publicado: pedido #{} -> {} (particion {}, offset {})",
                                evento.idPedido(),
                                evento.estadoNuevo(),
                                resultado.getRecordMetadata().partition(),
                                resultado.getRecordMetadata().offset());
                    }
                });
    }
}
