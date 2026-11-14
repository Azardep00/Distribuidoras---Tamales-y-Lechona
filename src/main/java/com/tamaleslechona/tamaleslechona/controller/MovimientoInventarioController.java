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
import org.springframework.web.bind.annotation.*;

import com.tamaleslechona.tamaleslechona.dto.MovimientoEntradaRequest;
import com.tamaleslechona.tamaleslechona.dto.MovimientoSalidaRequest;
import com.tamaleslechona.tamaleslechona.model.MovimientoInventario;
import com.tamaleslechona.tamaleslechona.model.TipoMovimiento;
import com.tamaleslechona.tamaleslechona.service.MovimientoInventarioService;

@Tag(name = "Inventario", description = "Entradas, salidas y reversiones de stock. Exige rol EMPLEADO.")
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(MovimientoInventarioService service) {
        this.service = service;
    }

    // Filtros opcionales: /api/movimientos?idProducto=<id>  o  /api/movimientos?tipo=SALIDA
    @Operation(summary = "Listar movimientos (filtros opcionales: idProducto, tipo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping
    public List<MovimientoInventario> listar(
            @RequestParam(required = false) String idProducto,
            @RequestParam(required = false) TipoMovimiento tipo) {
        if (idProducto != null) return service.listarPorProducto(idProducto);
        if (tipo != null) return service.listarPorTipo(tipo);
        return service.listar();
    }

    @Operation(summary = "Buscar un movimiento por su id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @GetMapping("/{id}")
    public MovimientoInventario buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Registrar una entrada de inventario (suma stock)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PostMapping("/entradas")
    public ResponseEntity<MovimientoInventario> registrarEntrada(
            @RequestBody MovimientoEntradaRequest body) {
        MovimientoInventario creado =
                service.registrarEntrada(body.idProducto(), body.idProveedor(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Registrar una salida de inventario (resta stock)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PostMapping("/salidas")
    public ResponseEntity<MovimientoInventario> registrarSalida(
            @RequestBody MovimientoSalidaRequest body) {
        MovimientoInventario creado =
                service.registrarSalida(body.idProducto(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Registrar una reversión (devuelve stock, sin proveedor)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PostMapping("/reversiones")
    public ResponseEntity<MovimientoInventario> registrarReversion(
            @RequestBody MovimientoSalidaRequest body) {
        MovimientoInventario creado =
                service.registrarReversion(body.idProducto(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
