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

import com.tamaleslechona.tamaleslechona.model.Lechona;
import com.tamaleslechona.tamaleslechona.model.Producto;
import com.tamaleslechona.tamaleslechona.model.Tamal;
import com.tamaleslechona.tamaleslechona.service.ProductoService;

@Tag(name = "Productos", description = "Catálogo de tamales y lechonas. Leer es público; crear, editar y desactivar exige rol EMPLEADO.")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar productos (por defecto solo activos; incluirInactivos=true para todos)")
    @ApiResponse(responseCode = "200", description = "OK")
    @SecurityRequirements
    @GetMapping
    public List<Producto> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @Operation(summary = "Buscar un producto por su id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @SecurityRequirements
    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponse(responseCode = "200", description = "OK")
    @SecurityRequirements
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @Operation(summary = "Crear un tamal")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Ejemplo", value = """
        {
          "tipoProducto": "Tamal",
          "nombre": "Tamal de cerdo",
          "descripcion": "Tamal tradicional tolimense",
          "precio": 8500,
          "stock": 20,
          "estado": true,
          "tipo": "NORMAL",
          "tamano": "GRANDE"
        }""")))
    @PostMapping("/tamales")
    public ResponseEntity<Producto> crearTamal(@RequestBody Tamal tamal) {
        Producto creado = service.registrar(tamal);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Crear una lechona")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Ejemplo", value = """
        {
          "tipoProducto": "Lechona",
          "nombre": "Lechona familiar",
          "descripcion": "Lechona tradicional tolimense",
          "precio": 120000,
          "stock": 5,
          "estado": true,
          "tamano": "MEDIANA",
          "numeroPorciones": 12
        }""")))
    @PostMapping("/lechonas")
    public ResponseEntity<Producto> crearLechona(@RequestBody Lechona lechona) {
        Producto creado = service.registrar(lechona);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un producto (no se puede cambiar su tipo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable String id, @RequestBody Producto nuevo) {
        return service.actualizar(id, nuevo);
    }

    @Operation(summary = "Desactivar un producto (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
