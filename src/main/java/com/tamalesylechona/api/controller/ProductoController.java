package main.java.com.tamalesylechona.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tamalesylechona.api.model.Lechona;
import com.tamalesylechona.api.model.Producto;
import com.tamalesylechona.api.model.Tamal;
import com.tamalesylechona.api.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Producto> listar(
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @PostMapping("/tamales")
    public ResponseEntity<Producto> crearTamal(@RequestBody Tamal tamal) {
        Producto creado = service.registrar(tamal);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/lechonas")
    public ResponseEntity<Producto> crearLechona(@RequestBody Lechona lechona) {
        Producto creado = service.registrar(lechona);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable int id, @RequestBody Producto nuevo) {
        return service.actualizar(id, nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable int id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
