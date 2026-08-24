package controller;

import model.Usuario;
import model.Cliente;
import model.Empleado;
import model.IActualizableUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private List<Usuario> usuarios;

    public UsuarioController() {
        this.usuarios = new ArrayList<>();
    }

    // ---- CREATE ----
    public void agregarUsuario(Usuario usuario) {
        if (buscarPorId(usuario.getIdUsuario()).isPresent()) {
            System.out.println("Ya existe un usuario con el id: " + usuario.getIdUsuario());
            return;
        }
        usuarios.add(usuario);
        System.out.println("Usuario agregado: " + usuario.getNombre() + " " + usuario.getApellido());
    }

    // ---- READ ----
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(int idUsuario) {
        return usuarios.stream()
                .filter(u -> u.getIdUsuario() == idUsuario)
                .findFirst();
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Cliente) {
                clientes.add((Cliente) u);
            }
        }
        return clientes;
    }

    public List<Empleado> listarEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Empleado) {
                empleados.add((Empleado) u);
            }
        }
        return empleados;
    }

    // ---- UPDATE ----
    public boolean actualizarUsuario(int idUsuario, Usuario usuarioConNuevosDatos) {
        Optional<Usuario> encontrado = buscarPorId(idUsuario);
        if (encontrado.isPresent()) {
            Usuario usuarioActual = encontrado.get();

            // Actualiza los datos comunes heredados de Usuario
            usuarioActual.actualizarDatos(
                    usuarioConNuevosDatos.getNombre(),
                    usuarioConNuevosDatos.getApellido(),
                    usuarioConNuevosDatos.getTelefono(),
                    usuarioConNuevosDatos.getCorreo()
            );

            // Actualiza los datos propios de la subclase (Cliente o Empleado)
            if (usuarioActual instanceof IActualizableUsuario) {
                ((IActualizableUsuario) usuarioActual).actualizarDatos(usuarioConNuevosDatos);
            }

            System.out.println("Usuario actualizado: id " + idUsuario);
            return true;
        }
        System.out.println("No se encontro el usuario con id: " + idUsuario);
        return false;
    }

    // ---- DELETE ----
    public boolean eliminarUsuario(int idUsuario) {
        Optional<Usuario> encontrado = buscarPorId(idUsuario);
        if (encontrado.isPresent()) {
            usuarios.remove(encontrado.get());
            System.out.println("Usuario eliminado: id " + idUsuario);
            return true;
        }
        System.out.println("No se encontro el usuario con id: " + idUsuario);
        return false;
    }

    // ---- Extra: login delegado a Usuario ----
    public Usuario iniciarSesion(String correo, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.iniciarSesion(correo, contrasena)) {
                return u;
            }
        }
        return null;
    }
}