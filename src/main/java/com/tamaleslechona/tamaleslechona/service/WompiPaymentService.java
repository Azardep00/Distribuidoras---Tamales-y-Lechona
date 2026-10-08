package com.tamaleslechona.tamaleslechona.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tamaleslechona.tamaleslechona.dto.DetallePedidoRequest;
import com.tamaleslechona.tamaleslechona.dto.WompiCheckoutResponse;
import com.tamaleslechona.tamaleslechona.dto.WompiEstadoPagoResponse;
import com.tamaleslechona.tamaleslechona.model.EstadoPagoWompi;
import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.PagoWompi;
import com.tamaleslechona.tamaleslechona.model.Pedido;
import com.tamaleslechona.tamaleslechona.repository.PagoWompiRepository;
import com.tamaleslechona.tamaleslechona.repository.PedidoRepository;

@Service
public class WompiPaymentService {

    private static final Logger log = LoggerFactory.getLogger(WompiPaymentService.class);
    private static final String MONEDA = "COP";

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final PagoWompiRepository pagoRepository;
    private final RestClient wompiClient;
    private final String privateKey;
    private final String integritySecret;
    private final String eventsSecret;

    public WompiPaymentService(
            PedidoService pedidoService,
            PedidoRepository pedidoRepository,
            PagoWompiRepository pagoRepository,
            RestClient.Builder restClientBuilder,
            @Value("${wompi.api-url}") String apiUrl,
            @Value("${wompi.private-key:}") String privateKey,
            @Value("${wompi.integrity-secret:}") String integritySecret,
            @Value("${wompi.events-secret:}") String eventsSecret) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.wompiClient = restClientBuilder.baseUrl(apiUrl).build();
        this.privateKey = privateKey;
        this.integritySecret = integritySecret;
        this.eventsSecret = eventsSecret;
    }

    @Transactional
    public WompiCheckoutResponse crearCheckout(int idCliente, List<DetallePedidoRequest> detalles) {
        exigirConfiguracionWompi();

        Pedido pedido = pedidoService.crear(idCliente, detalles);
        return crearIntento(pedido);
    }

    @Transactional
    public WompiCheckoutResponse crearIntentoParaPedido(int idPedido, int idCliente) {
        exigirConfiguracionWompi();
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pedido indicado."));
        validarPropietario(pedido, idCliente);

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalArgumentException("Solo puedes pagar un pedido pendiente.");
        }

        PagoWompi ultimoPago = pagoRepository
                .findFirstByPedido_IdPedidoOrderByIdPagoWompiDesc(idPedido)
                .orElse(null);
        if (ultimoPago != null && ultimoPago.getEstado() == EstadoPagoWompi.APPROVED) {
            throw new IllegalArgumentException("El pedido ya tiene un pago aprobado.");
        }
        if (ultimoPago != null && ultimoPago.getEstado() == EstadoPagoWompi.PENDING) {
            return crearRespuestaCheckout(ultimoPago);
        }

        return crearIntento(pedido);
    }

    private WompiCheckoutResponse crearIntento(Pedido pedido) {
        long montoEnCentavos = pedido.getTotal().multiply(BigDecimal.valueOf(100)).longValueExact();
        if (montoEnCentavos <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser mayor que cero.");
        }

        String referencia = "PED-" + pedido.getIdPedido() + "-" + UUID.randomUUID();
        PagoWompi pago = pagoRepository.save(new PagoWompi(pedido, referencia, montoEnCentavos, MONEDA));
        return crearRespuestaCheckout(pago);
    }

    @Transactional
    public WompiEstadoPagoResponse consultarTransaccion(
            String idTransaccion, String referencia, int idCliente) {
        validarIdTransaccion(idTransaccion);

        PagoWompi pago = pagoRepository.findFirstByReferencia(referencia)
                .orElseThrow(() -> new IllegalArgumentException("No existe un pago con esa referencia."));
        validarPropietario(pago.getPedido(), idCliente);
        JsonNode transaccion = consultarEnWompi(idTransaccion);

        pago = pagoRepository.buscarPorReferenciaParaActualizar(referencia)
                .orElseThrow(() -> new IllegalArgumentException("No existe un pago con esa referencia."));
        return aplicarTransaccion(pago, transaccion);
    }

    @Transactional
    public void recibirEvento(JsonNode evento, String checksumHeader) {
        exigirConfiguracion(eventsSecret, "WOMPI_EVENTS_SECRET");
        WompiEventSignature.validar(evento, checksumHeader, eventsSecret);

        if (!"transaction.updated".equals(evento.path("event").asText())) {
            return;
        }

        JsonNode eventoTransaccion = evento.path("data").path("transaction");
        String idTransaccion = eventoTransaccion.path("id").asText();
        validarIdTransaccion(idTransaccion);
        JsonNode transaccion = consultarEnWompi(idTransaccion);
        String referencia = transaccion.path("reference").asText();
        PagoWompi pago = pagoRepository.buscarPorReferenciaParaActualizar(referencia).orElse(null);
        if (pago == null) {
            log.warn("Evento Wompi recibido para una referencia desconocida: {}", referencia);
            return;
        }

        aplicarTransaccion(pago, transaccion);
    }

    private WompiCheckoutResponse crearRespuestaCheckout(PagoWompi pago) {
        String cadena = pago.getReferencia() + pago.getMontoEnCentavos() + pago.getMoneda() + integritySecret;
        return new WompiCheckoutResponse(
                pago.getPedido().getIdPedido(),
                pago.getReferencia(),
                pago.getMontoEnCentavos(),
                pago.getMoneda(),
                sha256(cadena));
    }

    private WompiEstadoPagoResponse aplicarTransaccion(PagoWompi pago, JsonNode transaccion) {
        String idTransaccion = transaccion.path("id").asText();
        String referencia = transaccion.path("reference").asText();
        long monto = transaccion.path("amount_in_cents").asLong(-1);
        String moneda = transaccion.path("currency").asText();
        EstadoPagoWompi nuevoEstado = mapearEstado(transaccion.path("status").asText());

        if (!pago.getReferencia().equals(referencia)
                || pago.getMontoEnCentavos() != monto
                || !pago.getMoneda().equals(moneda)) {
            throw new IllegalArgumentException("Los datos de la transacción Wompi no coinciden con el pedido.");
        }
        if (idTransaccion.isBlank()) {
            throw new IllegalArgumentException("Wompi no devolvió el identificador de la transacción.");
        }
        if (pago.getIdTransaccion() != null && !pago.getIdTransaccion().equals(idTransaccion)) {
            throw new IllegalArgumentException("La referencia ya está asociada a otra transacción.");
        }

        if (pago.getEstado() != EstadoPagoWompi.APPROVED) {
            pago.actualizar(idTransaccion, nuevoEstado);
            pagoRepository.save(pago);
        }

        Pedido pedido = pago.getPedido();
        if (nuevoEstado == EstadoPagoWompi.APPROVED && pedido.getEstado() == EstadoPedido.PENDIENTE) {
            pedidoService.cambiarEstado(pedido.getIdPedido(), EstadoPedido.CONFIRMADO);
        } else if (nuevoEstado == EstadoPagoWompi.APPROVED && pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalArgumentException("El pedido no puede confirmarse desde su estado actual.");
        }

        return new WompiEstadoPagoResponse(
                pedido.getIdPedido(),
                pago.getReferencia(),
                pago.getEstado().name(),
                pedido.getEstado().name());
    }

    private EstadoPagoWompi mapearEstado(String estado) {
        try {
            return EstadoPagoWompi.valueOf(estado.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wompi devolvió un estado de pago no reconocido.", e);
        }
    }

    private JsonNode consultarEnWompi(String idTransaccion) {
        exigirConfiguracion(privateKey, "WOMPI_PRIVATE_KEY");
        JsonNode respuesta = wompiClient.get()
                .uri("/v1/transactions/{id}", idTransaccion)
                .headers(headers -> headers.setBearerAuth(privateKey))
                .retrieve()
                .body(JsonNode.class);
        JsonNode transaccion = respuesta == null ? null : respuesta.path("data");
        if (transaccion == null || transaccion.isMissingNode() || transaccion.isNull()) {
            throw new IllegalStateException("Wompi respondió sin información de la transacción.");
        }
        return transaccion;
    }

    private void validarIdTransaccion(String idTransaccion) {
        if (idTransaccion == null || !idTransaccion.matches("[A-Za-z0-9-]{1,80}")) {
            throw new IllegalArgumentException("El identificador de la transacción no es válido.");
        }
    }

    private void validarPropietario(Pedido pedido, int idCliente) {
        if (pedido.getCliente().getIdUsuario() != idCliente) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Este pago no pertenece al cliente autenticado.");
        }
    }

    private void exigirConfiguracion(String valor, String variable) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Configura " + variable + " en el backend antes de usar Wompi.");
        }
    }

    private void exigirConfiguracionWompi() {
        exigirConfiguracion(privateKey, "WOMPI_PRIVATE_KEY");
        exigirConfiguracion(integritySecret, "WOMPI_INTEGRITY_SECRET");
        exigirConfiguracion(eventsSecret, "WOMPI_EVENTS_SECRET");
    }

    private static String sha256(String valor) {
        return WompiEventSignature.sha256(valor);
    }
}
