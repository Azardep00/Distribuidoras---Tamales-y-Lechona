package controller;

import java.util.*;

import model.*;
import repository.*;

public class UsuarioController {
    private final IUsuarioRepository repo;
    private int siguienteId = 1;

    public UsuarioController(IUsuarioRepository repo) {
        this.repo = repo;
    }

    public void registrar(Usuario u) {
        validar(u);
        u.setIdUsuario(siguienteId++);
        if (u instanceof Cliente c) c.setIdCliente(u.getIdUsuario());
        repo.guardar(u);
    }

    public List<Usuario> listar() {
        return repo.listarTodos().stream()
                .sorted(Comparator.comparingInt(Usuario::getIdUsuario))
                .toList();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return repo.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return listar().stream()
                .filter(Cliente.class::isInstance)
                .map(Cliente.class::cast)
                .filter(Usuario::isEstado)
                .toList();
    }

    public void actualizar(int id, Usuario nuevo) {
        Usuario actual =
                buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        validar(nuevo);
        if (repo.existeCorreo(nuevo.getCorreo(), id))
            throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
        actual.actualizarDatos(
                nuevo.getNombre(), nuevo.getApellido(), nuevo.getTelefono(), nuevo.getCorreo());
        actual.setFechaNacimiento(nuevo.getFechaNacimiento());
        if (nuevo instanceof Cliente nc && actual instanceof Cliente ac) {
            ac.setTipoCliente(nc.getTipoCliente());
            ac.setDireccion(nc.getDireccion());
        }
        if (nuevo instanceof Empleado ne && actual instanceof Empleado ae) {
            ae.setCargo(ne.getCargo());
            ae.setFechaContratacion(ne.getFechaContratacion());
        }
        if (nuevo.isEstado() != actual.isEstado()) actual.setEstado(nuevo.isEstado());
    }

    public void desactivar(int id) {
        Usuario u =
                buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        u.setEstado(false);
    }

    private void validar(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Usuario inválido.");
        if (u.getNombre() == null
                || u.getNombre().isBlank()
                || u.getApellido() == null
                || u.getApellido().isBlank())
            throw new IllegalArgumentException("Nombre y apellido son obligatorios.");
        if (u.getTelefono() == null || u.getTelefono().isBlank())
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        if (u.getCorreo() == null || !u.getCorreo().contains("@"))
            throw new IllegalArgumentException("Correo inválido.");
    }
}
