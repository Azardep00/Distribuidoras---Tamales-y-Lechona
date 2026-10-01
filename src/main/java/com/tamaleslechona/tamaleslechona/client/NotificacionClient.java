package com.tamaleslechona.tamaleslechona.client;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificacionClient {

    private final RestClient restClient;

    public NotificacionClient(@Value("${microservicio.notificaciones.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public void notificarCambioEstado(Integer idPedido, String correoCliente, String estadoNuevo) {
        restClient.post()
                .uri("/notificaciones/pedido")
                .body(Map.of(
                        "idPedido", idPedido,
                        "correoCliente", correoCliente,
                        "estadoNuevo", estadoNuevo))
                .retrieve()
                .toBodilessEntity();
    }
}