package tamaleslechona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tamaleslechona.dto.CambiarContrasenaRequest;
import tamaleslechona.dto.LoginRequest;
import tamaleslechona.dto.LoginResponse;
import tamaleslechona.model.Cliente;
import tamaleslechona.model.Empleado;
import tamaleslechona.model.Usuario;
import tamaleslechona.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuario> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public Usuario buscarPorCorreo(@RequestParam String correo) {
        return service.buscarPorCorreo(correo);
    }

    @PostMapping("/clientes")
    public ResponseEntity<Usuario> crearCliente(@RequestBody Cliente cliente) {
        Usuario creado = service.registrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PostMapping("/empleados")
    public ResponseEntity<Usuario> crearEmpleado(@RequestBody Empleado empleado) {
        Usuario creado = service.registrar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable int id, @RequestBody Usuario nuevo) {
        return service.actualizar(id, nuevo);
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable int id, @RequestBody CambiarContrasenaRequest body) {
        service.cambiarContrasena(id, body.contrasenaActual(), body.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable int id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest body) {
        return service.iniciarSesion(body.correo(), body.contrasena());
    }
}
