package view;

import model.Proveedor;

import java.util.List;

/**
 * PRINCIPIO SRP (Single Responsibility Principle)
 * Antes, ProveedorController tenía dos razones para cambiar:
 * 1) cómo se guardan/buscan los proveedores (lógica de negocio)
 * 2) cómo se muestran en consola (presentación)
 * Esta clase asume únicamente la responsabilidad de "mostrar en consola".
 * Si mañana la interfaz cambia a una app web, solo se reemplaza esta clase.
 */
public class ProveedorView {

    public static void mostrarProveedores(List<Proveedor> proveedores) {
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }

        for (Proveedor proveedor : proveedores) {
            System.out.println("ID: " + proveedor.getIdProveedor());
            System.out.println("Nombre: " + proveedor.getNombre());
            System.out.println("Telefono: " + proveedor.getTelefono());
            System.out.println("Correo: " + proveedor.getCorreo());
            System.out.println("Direccion: " + proveedor.getDireccion());
            System.out.println("Estado: " + proveedor.isEstado());
            System.out.println("-----------------------------------------");
        }
    }

    public static void mostrarProveedorRegistrado() {
        System.out.println("Proveedor registrado correctamente.");
    }

    public static void mostrarProveedorActualizado() {
        System.out.println("Proveedor actualizado correctamente.");
    }

    public static void mostrarProveedorEliminado() {
        System.out.println("Proveedor eliminado correctamente.");
    }

    public static void mostrarProveedorNoEncontrado() {
        System.out.println("No se encontro un proveedor con ese ID.");
    }
}
