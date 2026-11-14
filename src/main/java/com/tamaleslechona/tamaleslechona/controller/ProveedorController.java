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

import com.tamaleslechona.tamaleslechona.model.Proveedor;
import com.tamaleslechona.tamaleslechona.service.ProveedorService;

@Tag(name = "Proveedores", description = "Gestión de proveedores. Todo el grupo exige rol EMPLEADO.")
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    @Operation(summary = "Listar proveedores")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping
    public List<Proveedor> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @Operation(summary = "Buscar un proveedor por su id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @GetMapping("/{id}")
    public Proveedor buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Buscar proveedores por nombre, teléfono o correo")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping("/buscar")
    public List<Proveedor> buscar(@RequestParam String q) {
        return service.buscar(q);
    }

    @Operation(summary = "Crear un proveedor")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Ejemplo", value = """
        {
          "nombre": "Cerdos del Tolima",
          "telefono": "3001112233",
          "correo": "ventas@cerdostolima.com",
          "direccion": "Calle 10 # 5-20, Ibagué",
          "estado": true
        }""")))
    @PostMapping
    public ResponseEntity<Proveedor> registrar(@RequestBody Proveedor proveedor) {
        Proveedor creado = service.registrar(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un proveedor")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PutMapping("/{id}")
    public Proveedor actualizar(@PathVariable String id, @RequestBody Proveedor nuevo) {
        return service.actualizar(id, nuevo);
    }

    // Borrado físico (204). Responde 409 si el proveedor tiene movimientos.
    @Operation(summary = "Eliminar un proveedor (borrado físico; 409 si tiene movimientos)")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual de los datos")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Borrado lógico: conserva el registro pero lo marca como inactivo.
    @Operation(summary = "Desactivar un proveedor (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
