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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tamaleslechona.tamaleslechona.dto.CambiarContrasenaRequest;
import com.tamaleslechona.tamaleslechona.dto.LoginRequest;
import com.tamaleslechona.tamaleslechona.dto.LoginResponse;
import com.tamaleslechona.tamaleslechona.dto.RefreshRequest;
import com.tamaleslechona.tamaleslechona.model.Cliente;
import com.tamaleslechona.tamaleslechona.model.Empleado;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import com.tamaleslechona.tamaleslechona.service.UsuarioService;
import com.tamaleslechona.tamaleslechona.exception.CredencialesInvalidasException;
import com.tamaleslechona.tamaleslechona.security.LimitadorLogin;
import com.tamaleslechona.tamaleslechona.dto.RefreshRequest;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Usuarios y sesión", description = "Registro, inicio de sesión (JWT + refresh token) y gestión de usuarios.")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final LimitadorLogin limitador;

    public UsuarioController(UsuarioService service, LimitadorLogin limitador) {
        this.service = service;
        this.limitador = limitador;
    }

    // Solo empleados llegan aquí (ver SecurityConfig: GET /api/usuarios -> hasRole EMPLEADO).
    @Operation(summary = "Listar usuarios (solo empleados)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @GetMapping
    public List<Usuario> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    // Un cliente solo puede ver SU PROPIO perfil; un empleado puede ver cualquiera.
    @Operation(summary = "Ver un usuario (un cliente solo ve el suyo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable String id, Authentication auth) {
        verificarEsPropioOEmpleado(id, auth);
        return service.buscarPorId(id);
    }

    // Solo empleados (ver SecurityConfig): buscar a cualquiera por correo es
    // una acción de administración, no algo que un cliente deba poder hacer.
    @Operation(summary = "Buscar un usuario por correo (solo empleados)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @GetMapping("/buscar")
    public Usuario buscarPorCorreo(@RequestParam String correo) {
        return service.buscarPorCorreo(correo);
    }

    @Operation(summary = "Registrar un cliente (público)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @SecurityRequirements
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Ejemplo", value = """
        {
          "tipoUsuario": "Cliente",
          "nombre": "Laura",
          "apellido": "Ramírez",
          "telefono": "3001234567",
          "correo": "laura.nueva@example.com",
          "contrasena": "clave123",
          "estado": true,
          "fechaNacimiento": "1998-05-12",
          "tipoCliente": "NUEVO",
          "direccion": "Cra 5 # 10-20, Ibagué",
          "fechaRegistro": "2026-11-13"
        }""")))
    @PostMapping("/clientes")
    public ResponseEntity<Usuario> crearCliente(@RequestBody Cliente cliente) {
        Usuario creado = service.registrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Solo empleados (ver SecurityConfig): crear otro empleado es acción de administración.
    @Operation(summary = "Crear un empleado (solo empleados)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Ejemplo", value = """
        {
          "tipoUsuario": "Empleado",
          "nombre": "Carlos",
          "apellido": "Gómez",
          "telefono": "3009876543",
          "correo": "cajero@example.com",
          "contrasena": "clave123",
          "estado": true,
          "fechaNacimiento": "1990-03-02",
          "cargo": "Cajero",
          "fechaContratacion": "2026-11-13"
        }""")))
    @PostMapping("/empleados")
    public ResponseEntity<Usuario> crearEmpleado(@RequestBody Empleado empleado) {
        Usuario creado = service.registrar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Un cliente solo puede editar SU PROPIO perfil; un empleado puede editar cualquiera.
    @Operation(summary = "Actualizar un usuario (un cliente solo el suyo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable String id, @RequestBody Usuario nuevo, Authentication auth) {
        verificarEsPropioOEmpleado(id, auth);
        return service.actualizar(id, nuevo);
    }

    // Cambiar contraseña: solo el dueño de la cuenta (ni siquiera un empleado
    // debería poder cambiar la contraseña de otro sin pasar por "olvidé mi
    // contraseña", que no existe todavía — así que aquí exigimos ser el dueño).
    @Operation(summary = "Cambiar la contraseña propia")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable String id, @RequestBody CambiarContrasenaRequest body, Authentication auth) {
        if (!id.equals(idAutenticado(auth))) {
            throw new AccessDeniedException("Solo puedes cambiar tu propia contraseña.");
        }
        service.cambiarContrasena(id, body.contrasenaActual(), body.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }

    // Solo empleados (ver SecurityConfig): desactivar una cuenta es acción de administración.
    @Operation(summary = "Desactivar un usuario (borrado lógico, solo empleados)")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para esta acción")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar sesión: devuelve el token JWT y el refresh token")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @ApiResponse(responseCode = "429", description = "Demasiados intentos")
    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest body, HttpServletRequest request) {
        String ip = ipCliente(request);
        String correo = body.correo();

        // 1) Si la IP o el correo estan bloqueados, responde 429 sin mas.
        limitador.verificarNoBloqueado(ip, correo);

        try {
            LoginResponse respuesta = service.iniciarSesion(correo, body.contrasena());
            // 2) Login correcto: se reinicia el contador de ese correo.
            limitador.registrarExito(correo);
            return respuesta;
        } catch (CredencialesInvalidasException e) {
            // 3) Credenciales malas: se cuenta el fallo y se sigue con el 401 normal.
            limitador.registrarFallo(ip, correo);
            throw e;
        }
    }

    @Operation(summary = "Renovar la sesión con un refresh token")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta el token o no es válido")
    @SecurityRequirements
    @PostMapping("/refresh")
    public LoginResponse refrescar(@RequestBody RefreshRequest body) {
        return service.refrescarSesion(body.refreshToken());
    }

    @Operation(summary = "Cerrar sesión (revoca el refresh token)")
    @ApiResponse(responseCode = "204", description = "Operación realizada (sin contenido)")
    @SecurityRequirements
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest body) {
        service.cerrarSesion(body.refreshToken());
        return ResponseEntity.noContent().build();
    }

    // Detras del proxy de Render, getRemoteAddr() devuelve la IP del proxy, no
    // la del usuario. La IP real viene en X-Forwarded-For (la primera de la lista).
    private String ipCliente(HttpServletRequest request) {
        String reenviada = request.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.isBlank()) {
            return reenviada.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void verificarEsPropioOEmpleado(String idSolicitado, Authentication auth) {
        boolean esEmpleado = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));
        if (!esEmpleado && !idSolicitado.equals(idAutenticado(auth))) {
            throw new AccessDeniedException("No puedes acceder al perfil de otro usuario.");
        }
    }

    private String idAutenticado(Authentication auth) {
        return auth.getName();
    }
}
