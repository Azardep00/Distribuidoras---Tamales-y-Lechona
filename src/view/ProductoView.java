package view;

import model.Producto;
import java.util.List;

public class ProductoView {

    public static void mostrarProductos(List<Producto> productos) {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        for (Producto p : productos) {
            System.out.println("ID: " + p.getIdProducto() +
                    " | " + p.getNombre() +
                    " | Tipo: " + p.getClass().getSimpleName() +
                    " | Precio: " + p.getPrecio() +
                    " | Stock: " + p.getStock() +
                    " | Activo: " + p.isEstado());
        }
    }

    public static void mostrarProductoRegistrado() {
        System.out.println("Producto registrado correctamente.");
    }

    public static void mostrarProductoActualizado() {
        System.out.println("Producto actualizado correctamente.");
    }

    public static void mostrarProductoEliminado() {
        System.out.println("Producto eliminado correctamente.");
    }

    public static void mostrarProductoNoEncontrado() {
        System.out.println("No se encontró un producto con ese ID.");
    }
}