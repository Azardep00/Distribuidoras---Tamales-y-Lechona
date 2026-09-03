package controller;

import model.IActualizableProducto;
import model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoController {

    private static final List<Producto> productos = new ArrayList<>();
    private static int siguienteId = 1;

    public static void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (producto.getIdProducto() <= 0) {
            producto.setIdProducto(siguienteId++);
        } else {
            if (existeId(producto.getIdProducto())) {
                throw new IllegalArgumentException("Ya existe un producto con ese ID.");
            }
            siguienteId = Math.max(siguienteId, producto.getIdProducto() + 1);
        }

        validar(producto);
        productos.add(producto);
    }

    public static List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    private static boolean existeId(int id) {
        return productos.stream().anyMatch(p -> p.getIdProducto() == id);
    }

    public static Producto buscarProducto(int id) {
        return productos.stream()
                .filter(p -> p.getIdProducto() == id)
                .findFirst()
                .orElse(null);
    }

    public static Producto buscarProducto(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public static void eliminarProducto(int id) {
        Producto p = buscarProducto(id);
        if (p != null) {
            productos.remove(p);
        } else {
            throw new IllegalArgumentException("No se encontró el producto.");
        }
    }

    public static void actualizarProducto(Producto nuevo) {
        if (nuevo == null) {
            throw new IllegalArgumentException("Producto inválido.");
        }

        Producto actual = buscarProducto(nuevo.getIdProducto());
        if (actual == null) {
            throw new IllegalArgumentException("No se encontró el producto.");
        }

        validar(nuevo);
        actual.setNombre(nuevo.getNombre());
        actual.setDescripcion(nuevo.getDescripcion());
        actual.setPrecio(nuevo.getPrecio());
        actual.setStock(nuevo.getStock());

        if (actual instanceof IActualizableProducto a) {
            a.actualizarDatos(nuevo);
        }
    }

    public static boolean consultarDisponibilidad(int id, int cantidad) {
        Producto p = buscarProducto(id);
        return p != null && p.consultarDisponibilidad(cantidad);
    }

    private static void validar(Producto p) {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (p.getPrecio() == null || p.getPrecio().signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}