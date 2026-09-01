package view;

import model.Usuario;
import java.util.List;

public class UsuarioView {

    public static void mostrarUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : usuarios) {
            System.out.println(u);
        }
    }

    public static void mostrarUsuarioRegistrado(String nombreCompleto) {
        System.out.println("Usuario registrado: " + nombreCompleto);
    }

    public static void mostrarUsuarioActualizado() {
        System.out.println("Usuario actualizado correctamente.");
    }

    public static void mostrarUsuarioEliminado() {
        System.out.println("Usuario eliminado correctamente.");
    }

    public static void mostrarUsuarioNoEncontrado() {
        System.out.println("No se encontró un usuario con ese ID.");
    }
}