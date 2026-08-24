package controller;

import model.IActualizableProducto;
import model.Producto;
import java.util.ArrayList;
import java.util.List;

public class ProductoController {

    private static List<Producto> productos = new ArrayList<>();

    public static void agregarProducto(Producto producto) throws RuntimeException {
        if (producto != null) {
            if (!existeId(producto.getIdProducto())) {
                productos.add(producto);
                System.out.println("Producto agregado correctamente");
            } else {
                throw new RuntimeException("Error: Ya existe un producto con ese ID");
            }
        } else {
            throw new RuntimeException("Error: producto nulo");
        }
    }

    public List<Producto> listarProductos() {
        return productos;
    }

    private static boolean existeId(int idProducto) {
        for (Producto p : productos) {
            if (p.getIdProducto() == idProducto) {
                return true;
            }
        }
        return false;
    }

    public static void eliminarProducto(int idProducto) {
        for (Producto producto : productos) {
            if (producto.getIdProducto() == idProducto) {
                productos.remove(producto);
                System.out.println("Producto eliminado correctamente");
                return;
            }
        }
        System.out.println("No se encontro ningun producto con ese ID");
    }

    public static void eliminarProducto(String nombre) {
        productos.removeIf(producto -> nombre.equalsIgnoreCase(producto.getNombre()));
    }

    public static Producto buscarProducto(int idProducto) {
        for (Producto producto : productos) {
            if (producto.getIdProducto() == idProducto) {
                return producto;
            }
        }
        return null;
    }

    public static Producto buscarProducto(String nombre) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
    }

    public static void actualizarProducto(Producto producto) {
        boolean actualizo = false;

        for (Producto p : productos) {
            if (p.getIdProducto() == producto.getIdProducto()) {
                p.setNombre(producto.getNombre());
                p.setDescripcion(producto.getDescripcion());
                p.actualizarPrecio(producto.getPrecio());
                p.cambiarEstado(producto.isEstado());

                // Datos específicos del subtipo
                if (p instanceof IActualizableProducto) {
                    IActualizableProducto actualizable = (IActualizableProducto) p;
                    actualizable.actualizarDatos(producto);
                }

                System.out.println("Producto actualizado");
                actualizo = true;
                break;
            }
        }

        if (!actualizo) {
            System.out.println("No se encontro un producto con ese ID");
        }
    }
    public static boolean consultarDisponibilidad(int idProducto, int cantidadRequerida) {
        Producto producto = buscarProducto(idProducto);
        return producto != null && producto.consultarDisponibilidad(cantidadRequerida);
    }
}