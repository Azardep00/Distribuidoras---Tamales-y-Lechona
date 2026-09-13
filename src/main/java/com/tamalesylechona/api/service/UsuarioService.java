package main.java.com.tamalesylechona.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tamalesylechona.api.dto.LoginResponse;
import com.tamalesylechona.api.exception.CredencialesInvalidasException;
import com.tamalesylechona.api.exception.RecursoNoEncontradoException;
import com.tamalesylechona.api.model.Cliente;
import com.tamalesylechona.api.model.Empleado;
import com.tamalesylechona.api.model.Usuario;
import com.tamalesylechona.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario registrar(Usuario u) {
        validarDatosBasicos(u);
        if (repo.existsByCorreoIgnoreCase(u.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo.");
        }
        return repo.save(u);
    }

    public List<Usuario> listar(boolean incluirInactivos) {
        return incluirInactivos ? repo.findAll() : repo.findByEstadoTrue();
    }

    public Usuario buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    public Usuario buscarPorCorreo(String correo) {
        return repo.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    // Nota: esta actualización NUNCA toca la contraseña; para eso está
    // cambiarContrasena(...). Así evitamos que un PUT normal la pise sin querer.
    public Usuario actualizar(int id, Usuario nuevo) {
        Usuario actual = buscarPorId(id);
        if (!actual.getClass().equals(nuevo.getClass())) {
            throw new IllegalArgumentException("No puedes cambiar el tipo de usuario; crea uno nuevo.");
        }
        if (nuevo.getNombre() == null || nuevo.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (!actual.getCorreo().equalsIgnoreCase(nuevo.getCorreo())
                && repo.existsByCorreoIgnoreCase(nuevo.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo.");
        }

        actual.setNombre(nuevo.getNombre());
        actual.setApellido(nuevo.getApellido());
        actual.setTelefono(nuevo.getTelefono());
        actual.setCorreo(nuevo.getCorreo());
        actual.setFechaNacimiento(nuevo.getFechaNacimiento());

        if (nuevo instanceof Cliente c && actual instanceof Cliente a) {
            a.setTipoCliente(c.getTipoCliente());
            a.setDireccion(c.getDireccion());
            a.setFechaRegistro(c.getFechaRegistro());
        }
        if (nuevo instanceof Empleado e && actual instanceof Empleado a) {
            a.setCargo(e.getCargo());
            a.setFechaContratacion(e.getFechaContratacion());
        }
        return repo.save(actual);
    }

    public void cambiarContrasena(int id, String actualPlano, String nuevaPlano) {
        Usuario u = buscarPorId(id);
        u.cambiarContrasena(actualPlano, nuevaPlano);
        repo.save(u);
    }

    public void desactivar(int id) {
        Usuario u = buscarPorId(id);
        u.setEstado(false);
        repo.save(u);
    }

    public LoginResponse iniciarSesion(String correo, String contrasena) {
        Usuario u =
                repo.findByCorreoIgnoreCase(correo)
                        .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));
        if (!u.isEstado()) {
            throw new CredencialesInvalidasException("El usuario está inactivo.");
        }
        if (!u.verificarContrasena(contrasena)) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }
        return LoginResponse.desde(u);
    }

    private void validarDatosBasicos(Usuario u) {
        if (u == null || u.getNombre() == null || u.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (u.getCorreo() == null || u.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
    }
}
