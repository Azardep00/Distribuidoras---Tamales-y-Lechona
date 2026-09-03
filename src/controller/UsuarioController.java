package controller;

import model.*;

import java.util.*;

public class UsuarioController {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int siguienteId = 1;

    public void agregarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario inválido.");
        }
        if (usuarios.stream().anyMatch(u -> u.getCorreo().equalsIgnoreCase(usuario.getCorreo()))) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        if (usuario.getIdUsuario() <= 0) {
            usuario.setIdUsuario(siguienteId++);
        } else {
            siguienteId = Math.max(siguienteId, usuario.getIdUsuario() + 1);
        }

        if (usuario instanceof Cliente c && c.getIdCliente() <= 0) {
            c.setIdCliente(usuario.getIdUsuario());
        }

        usuarios.add(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getIdUsuario() == id)
                .findFirst();
    }

    public List<Cliente> listarClientes() {
        List<Cliente> r = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Cliente c) {
                r.add(c);
            }
        }
        return r;
    }

    public List<Empleado> listarEmpleados() {
        List<Empleado> r = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Empleado e) {
                r.add(e);
            }
        }
        return r;
    }

    public boolean actualizarUsuario(int id, Usuario nuevo) {
        Usuario actual = buscarPorId(id).orElse(null);
        if (actual == null || nuevo == null) {
            return false;
        }

        if (usuarios.stream().anyMatch(u -> u != actual && u.getCorreo().equalsIgnoreCase(nuevo.getCorreo()))) {
            throw new IllegalArgumentException("El correo ya pertenece a otro usuario.");
        }

        actual.actualizarDatos(nuevo.getNombre(), nuevo.getApellido(), nuevo.getTelefono(), nuevo.getCorreo());
        actual.setFechaNacimiento(nuevo.getFechaNacimiento());

        if (actual instanceof IActualizableUsuario a) {
            a.actualizarDatos(nuevo);
        }

        return true;
    }

    public boolean eliminarUsuario(int id) {
        Usuario u = buscarPorId(id).orElse(null);
        return u != null && usuarios.remove(u);
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        return usuarios.stream()
                .filter(u -> u.iniciarSesion(correo, contrasena))
                .findFirst()
                .orElse(null);
    }
}