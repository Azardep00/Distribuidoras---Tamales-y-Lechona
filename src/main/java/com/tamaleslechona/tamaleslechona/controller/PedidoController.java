package com.tamaleslechona.tamaleslechona.controller;

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

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // Filtros opcionales: /api/pedidos?idCliente=1  o  /api/pedidos?estado=PENDIENTE
    //
    // Un Cliente solo puede pedir SUS PROPIOS pedidos (idCliente debe ser el
    // suyo), y no puede pedir el listado completo sin filtrar — eso es vista
    // de administración. Un Empleado puede pedir lo que quiera.
    @GetMapping
    public List<Pedido> listar(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) EstadoPedido estado,
            Authentication auth) {

        if (!esEmpleado(auth)) {
            if (idCliente == null || idCliente != idAutenticado(auth)) {
                throw new AccessDeniedException("Solo puedes ver tus propios pedidos.");
            }
        }

        if (idCliente != null) return service.listarPorCliente(idCliente);
        if (estado != null) return service.listarPorEstado(estado);
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable int id, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);
        verificarEsDuenoOEmpleado(pedido, auth);
        return pedido;
    }

    // Si quien crea el pedido es un Cliente, se ignora cualquier idCliente
    // que venga en el body y se usa el del token: así un cliente nunca puede
    // crear un pedido a nombre de otro con solo cambiar un número en la petición.
    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody PedidoRequest body, Authentication auth) {
        int idCliente = esEmpleado(auth) ? body.idCliente() : idAutenticado(auth);
        Pedido creado = service.crear(idCliente, body.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable int id, @RequestBody CambiarEstadoPedidoRequest body) {
        // Ya protegido a nivel de ruta en SecurityConfig (solo ROLE_EMPLEADO
        // llega hasta acá), no hace falta repetir el chequeo aquí.
        return service.cambiarEstado(id, body.estado());
    }

    // Cancela el pedido y devuelve el stock descontado (no borra el registro).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);
        verificarEsDuenoOEmpleado(pedido, auth);
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarEsDuenoOEmpleado(Pedido pedido, Authentication auth) {
        if (esEmpleado(auth)) return;
        if (pedido.getCliente().getIdUsuario() != idAutenticado(auth)) {
            throw new AccessDeniedException("Este pedido no te pertenece.");
        }
    }

    private boolean esEmpleado(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
    }

    // El JwtAuthenticationFilter guarda el idUsuario como subject del token
    // (claims.getSubject()), que es lo que Authentication#getName() expone aquí.
    private int idAutenticado(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }
}
