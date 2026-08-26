package repository;

import model.Proveedor;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta que guarda los proveedores en memoria (ArrayList).
 * ProveedorController no conoce esta clase, solo conoce IProveedorRepository.
 */
public class ProveedorRepositoryMemoria implements IProveedorRepository {

    private List<Proveedor> proveedores;

    public ProveedorRepositoryMemoria() {
        this.proveedores = new ArrayList<>();
    }

    @Override
    public void guardar(Proveedor proveedor) {
        proveedores.add(proveedor);
    }

    @Override
    public List<Proveedor> listarTodos() {
        return proveedores;
    }

    @Override
    public Proveedor buscarPorId(int id) {
        for (Proveedor proveedor : proveedores) {
            if (proveedor.getIdProveedor() == id) {
                return proveedor;
            }
        }
        return null;
    }

    @Override
    public void eliminar(Proveedor proveedor) {
        proveedores.remove(proveedor);
    }
}
