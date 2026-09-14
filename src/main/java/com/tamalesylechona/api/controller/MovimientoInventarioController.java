package main.java.com.tamalesylechona.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tamalesylechona.api.dto.MovimientoEntradaRequest;
import com.tamalesylechona.api.dto.MovimientoSalidaRequest;
import com.tamalesylechona.api.model.MovimientoInventario;
import com.tamalesylechona.api.model.TipoMovimiento;
import com.tamalesylechona.api.service.MovimientoInventarioService;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(MovimientoInventarioService service) {
        this.service = service;
    }

    // Filtros opcionales: /api/movimientos?idProducto=1  o  /api/movimientos?tipo=SALIDA
    @GetMapping
    public List<MovimientoInventario> listar(
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) TipoMovimiento tipo) {
        if (idProducto != null) return service.listarPorProducto(idProducto);
        if (tipo != null) return service.listarPorTipo(tipo);
        return service.listar();
    }

    @GetMapping("/{id}")
    public MovimientoInventario buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/entradas")
    public ResponseEntity<MovimientoInventario> registrarEntrada(
            @RequestBody MovimientoEntradaRequest body) {
        MovimientoInventario creado =
                service.registrarEntrada(body.idProducto(), body.idProveedor(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/salidas")
    public ResponseEntity<MovimientoInventario> registrarSalida(
            @RequestBody MovimientoSalidaRequest body) {
        MovimientoInventario creado =
                service.registrarSalida(body.idProducto(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/reversiones")
    public ResponseEntity<MovimientoInventario> registrarReversion(
            @RequestBody MovimientoSalidaRequest body) {
        MovimientoInventario creado =
                service.registrarReversion(body.idProducto(), body.cantidad(), body.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
