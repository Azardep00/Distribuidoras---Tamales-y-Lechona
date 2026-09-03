package repository;

import java.util.*;

import model.Proveedor;

public class ProveedorRepositoryMemoria implements IProveedorRepository {
    private final List<Proveedor> datos = new ArrayList<>();

    public void guardar(Proveedor p) {
        datos.add(p);
    }

    public List<Proveedor> listarTodos() {
        return new ArrayList<>(datos);
    }

    public Proveedor buscarPorId(int id) {
        return datos.stream().filter(p -> p.getIdProveedor() == id).findFirst().orElse(null);
    }

    public void eliminar(Proveedor p) {
        datos.remove(p);
    }
}
