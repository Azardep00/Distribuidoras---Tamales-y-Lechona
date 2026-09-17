package com.tamaleslechona.tamaleslechona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping
    public List<Pedido> listar(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) EstadoPedido estado) {
        if (idCliente != null) return service.listarPorCliente(idCliente);
        if (estado != null) return service.listarPorEstado(estado);
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody PedidoRequest body) {
        Pedido creado = service.crear(body.idCliente(), body.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable int id, @RequestBody CambiarEstadoPedidoRequest body) {
        return service.cambiarEstado(id, body.estado());
    }

    // Cancela el pedido y devuelve el stock descontado (no borra el registro).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}