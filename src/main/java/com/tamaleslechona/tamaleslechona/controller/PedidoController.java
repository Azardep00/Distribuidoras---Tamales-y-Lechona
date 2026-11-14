package com.tamaleslechona.tamaleslechona.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tamaleslechona.tamaleslechona.dto.CambiarEstadoPedidoRequest;
import com.tamaleslechona.tamaleslechona.dto.PedidoRequest;
import com.tamaleslechona.tamaleslechona.model.EstadoPedido;
import com.tamaleslechona.tamaleslechona.model.Pedido;
import com.tamaleslechona.tamaleslechona.service.PedidoService;

@Tag(name = "Pedidos", description = "Un pedido descuenta stock al crearse. Un cliente solo ve y cancela los suyos.")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // Filtros opcionales: /api/pedidos?idCliente=<id>  o  /api/pedidos?estado=PENDIENTE
    //
    // Un Cliente solo puede pedir SUS PROPIOS pedidos (idCliente debe ser el
    // suyo), y no puede pedir el listado completo sin filtrar — eso es vista
    // de administración. Un Empleado puede pedir lo que quiera.
    @Operation(summary = "Listar pedidos (filtros opcionales: idCliente, estado)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping
    public List<Pedido> listar(
            @RequestParam(required = false) String idCliente,
            @RequestParam(required = false) EstadoPedido estado,
            Authentication auth) {

        if (!esEmpleado(auth)) {
            if (idCliente == null || !idCliente.equals(idAutenticado(auth))) {
                throw new AccessDeniedException("Solo puedes ver tus propios pedidos.");
            }
        }

        if (idCliente != null) return service.listarPorCliente(idCliente);
        if (estado != null) return service.listarPorEstado(estado);
        return service.listar();
    }

    @Operation(summary = "Buscar un pedido por su id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable String id, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);
        verificarEsDuenoOEmpleado(pedido, auth);
        return pedido;
    }

    // Si quien crea el pedido es un Cliente, se ignora cualquier idCliente
    // que venga en el body y se usa el del token: así un cliente nunca puede
    // crear un pedido a nombre de otro con solo cambiar un número en la petición.
    @Operation(summary = "Crear un pedido y descontar el stock (400 si no alcanza)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody PedidoRequest body, Authentication auth) {
        String idCliente = esEmpleado(auth) ? body.idCliente() : idAutenticado(auth);
        Pedido creado = service.crear(idCliente, body.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Avanzar el estado de un pedido un paso (solo empleados)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PatchMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable String id, @RequestBody CambiarEstadoPedidoRequest body) {
        // Ya protegido a nivel de ruta en SecurityConfig (solo ROLE_EMPLEADO
        // llega hasta acá), no hace falta repetir el chequeo aquí.
        return service.cambiarEstado(id, body.estado());
    }

    // Cancela el pedido y devuelve el stock descontado (no borra el registro).
    @Operation(summary = "Cancelar un pedido y devolver el stock")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable String id, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);
        verificarEsDuenoOEmpleado(pedido, auth);
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarEsDuenoOEmpleado(Pedido pedido, Authentication auth) {
        if (esEmpleado(auth)) return;
        if (!pedido.getIdCliente().equals(idAutenticado(auth))) {
            throw new AccessDeniedException("Este pedido no te pertenece.");
        }
    }

    private boolean esEmpleado(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
    }

    // El JwtAuthenticationFilter guarda el idUsuario como subject del token
    // (claims.getSubject()), que es lo que Authentication#getName() expone aquí.
    private String idAutenticado(Authentication auth) {
        return auth.getName();
    }
}
