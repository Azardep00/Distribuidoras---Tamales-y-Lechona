package tamaleslechona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tamaleslechona.model.Proveedor;
import tamaleslechona.service.ProveedorService;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Proveedor> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public Proveedor buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<Proveedor> buscar(@RequestParam String q) {
        return service.buscar(q);
    }

    @PostMapping
    public ResponseEntity<Proveedor> registrar(@RequestBody Proveedor proveedor) {
        Proveedor creado = service.registrar(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Proveedor actualizar(@PathVariable int id, @RequestBody Proveedor nuevo) {
        return service.actualizar(id, nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable int id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
