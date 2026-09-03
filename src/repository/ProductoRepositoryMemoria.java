package repository;

import java.util.*;

import model.Producto;

public class ProductoRepositoryMemoria implements IProductoRepository {
    private final List<Producto> datos = new ArrayList<>();

    public void guardar(Producto p) {
        datos.add(p);
    }

    public void actualizar(Producto p) {
        int i = datos.indexOf(p);
        if (i >= 0) datos.set(i, p);
    }

    public Optional<Producto> buscarPorId(int id) {
        return datos.stream().filter(p -> p.getIdProducto() == id).findFirst();
    }

    public List<Producto> listarTodos() {
        return new ArrayList<>(datos);
    }

    public void desactivar(int id) {
        buscarPorId(id).ifPresent(p -> p.setEstado(false));
    }
}
