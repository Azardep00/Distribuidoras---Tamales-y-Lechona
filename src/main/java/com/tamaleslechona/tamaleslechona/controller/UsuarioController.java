package com.tamaleslechona.tamaleslechona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tamaleslechona.tamaleslechona.dto.CambiarContrasenaRequest;
import com.tamaleslechona.tamaleslechona.dto.LoginRequest;
import com.tamaleslechona.tamaleslechona.dto.LoginResponse;
import com.tamaleslechona.tamaleslechona.model.Cliente;
import com.tamaleslechona.tamaleslechona.model.Empleado;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import com.tamaleslechona.tamaleslechona.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // Solo empleados llegan aquí (ver SecurityConfig: GET /api/usuarios -> hasRole EMPLEADO).
    @GetMapping
    public List<Usuario> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    // Un cliente solo puede ver SU PROPIO perfil; un empleado puede ver cualquiera.
    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable int id, Authentication auth) {
        verificarEsPropioOEmpleado(id, auth);
        return service.buscarPorId(id);
    }

    // Solo empleados (ver SecurityConfig): buscar a cualquiera por correo es
    // una acción de administración, no algo que un cliente deba poder hacer.
    @GetMapping("/buscar")
    public Usuario buscarPorCorreo(@RequestParam String correo) {
        return service.buscarPorCorreo(correo);
    }

    @PostMapping("/clientes")
    public ResponseEntity<Usuario> crearCliente(@RequestBody Cliente cliente) {
        Usuario creado = service.registrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Solo empleados (ver SecurityConfig): crear otro empleado es acción de administración.
    @PostMapping("/empleados")
    public ResponseEntity<Usuario> crearEmpleado(@RequestBody Empleado empleado) {
        Usuario creado = service.registrar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Un cliente solo puede editar SU PROPIO perfil; un empleado puede editar cualquiera.
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable int id, @RequestBody Usuario nuevo, Authentication auth) {
        verificarEsPropioOEmpleado(id, auth);
        return service.actualizar(id, nuevo);
    }

    // Cambiar contraseña: solo el dueño de la cuenta (ni siquiera un empleado
    // debería poder cambiar la contraseña de otro sin pasar por "olvidé mi
    // contraseña", que no existe todavía — así que aquí exigimos ser el dueño).
    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable int id, @RequestBody CambiarContrasenaRequest body, Authentication auth) {
        if (id != idAutenticado(auth)) {
            throw new AccessDeniedException("Solo puedes cambiar tu propia contraseña.");
        }
        service.cambiarContrasena(id, body.contrasenaActual(), body.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }

    // Solo empleados (ver SecurityConfig): desactivar una cuenta es acción de administración.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable int id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest body) {
        return service.iniciarSesion(body.correo(), body.contrasena());
    }

    private void verificarEsPropioOEmpleado(int idSolicitado, Authentication auth) {
        boolean esEmpleado = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
        if (!esEmpleado && idSolicitado != idAutenticado(auth)) {
            throw new AccessDeniedException("No puedes acceder al perfil de otro usuario.");
        }
    }

    private int idAutenticado(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }
}
