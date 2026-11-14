package com.tamaleslechona.tamaleslechona.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Pagos Wompi", description = "Pagos con Wompi (sandbox). Los endpoints de pago son solo para clientes.")
@RestController
@RequestMapping("/api/pagos/wompi")
public class WompiPaymentController {

    private final WompiPaymentService service;

    public WompiPaymentController(WompiPaymentService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un pedido e iniciar su pago (solo clientes)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @PostMapping("/checkout")
    public ResponseEntity<WompiCheckoutResponse> iniciarCheckout(
            @RequestBody PedidoRequest body, Authentication auth) {
        validarCliente(auth);
        WompiCheckoutResponse respuesta = service.crearCheckout(
                auth.getName(), body.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Crear un nuevo intento de pago para un pedido pendiente")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @PostMapping("/pedidos/{idPedido}/intentos")
    public WompiCheckoutResponse reintentarPago(@PathVariable String idPedido, Authentication auth) {
        validarCliente(auth);
        return service.crearIntentoParaPedido(idPedido, auth.getName());
    }

    @Operation(summary = "Consultar una transacción en Wompi y aplicar su estado")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping("/transacciones/{idTransaccion}")
    public WompiEstadoPagoResponse consultarTransaccion(
            @PathVariable String idTransaccion,
            @RequestParam String referencia,
            Authentication auth) {
        return service.consultarTransaccion(
                idTransaccion, referencia, auth.getName());
    }

    @Operation(summary = "Webhook de eventos de Wompi (firmado con checksum)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @SecurityRequirements
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
