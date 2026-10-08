package com.tamaleslechona.tamaleslechona.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.tamaleslechona.tamaleslechona.dto.PedidoRequest;
import com.tamaleslechona.tamaleslechona.dto.WompiCheckoutResponse;
import com.tamaleslechona.tamaleslechona.dto.WompiEstadoPagoResponse;
import com.tamaleslechona.tamaleslechona.service.WompiPaymentService;

@RestController
@RequestMapping("/api/pagos/wompi")
public class WompiPaymentController {

    private final WompiPaymentService service;

    public WompiPaymentController(WompiPaymentService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public ResponseEntity<WompiCheckoutResponse> iniciarCheckout(
            @RequestBody PedidoRequest body, Authentication auth) {
        validarCliente(auth);
        WompiCheckoutResponse respuesta = service.crearCheckout(
                Integer.parseInt(auth.getName()), body.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/pedidos/{idPedido}/intentos")
    public WompiCheckoutResponse reintentarPago(@PathVariable int idPedido, Authentication auth) {
        validarCliente(auth);
        return service.crearIntentoParaPedido(idPedido, Integer.parseInt(auth.getName()));
    }

    @GetMapping("/transacciones/{idTransaccion}")
    public WompiEstadoPagoResponse consultarTransaccion(
            @PathVariable String idTransaccion,
            @RequestParam String referencia,
            Authentication auth) {
        return service.consultarTransaccion(
                idTransaccion, referencia, Integer.parseInt(auth.getName()));
    }

    @PostMapping("/eventos")
    public ResponseEntity<Void> recibirEvento(
            @RequestBody JsonNode evento,
            @RequestHeader(value = "X-Event-Checksum", required = false) String checksum) {
        service.recibirEvento(evento, checksum);
        return ResponseEntity.ok().build();
    }

    private void validarCliente(Authentication auth) {
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            throw new AccessDeniedException("Solo los clientes pueden iniciar un pago Wompi.");
        }
    }
}
