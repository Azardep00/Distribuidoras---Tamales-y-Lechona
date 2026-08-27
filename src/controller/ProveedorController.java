package controller;

import model.Proveedor;
import repository.IProveedorRepository;

import java.util.List;

/**
 * PRINCIPIO SRP: ya no imprime nada en consola, solo maneja la lógica de negocio
 * de proveedores (registrar, buscar, actualizar, eliminar).
 * PRINCIPIO DIP: depende de la abstracción IProveedorRepository, no de un
 * ArrayList concreto. La implementación real se le "inyecta" por el constructor.
 */
public class ProveedorController {

    private final IProveedorRepository repositorio;
    private int siguienteId;

    public ProveedorController(IProveedorRepository repositorio) {
        this.repositorio = repositorio;
        this.siguienteId = 1;
    }

    public void registrarProveedor(Proveedor proveedor) {
        proveedor.setIdProveedor(siguienteId);
        repositorio.guardar(proveedor);
        siguienteId++;
    }

    public List<Proveedor> listarProveedores() {
        return repositorio.listarTodos();
    }

    public Proveedor buscarProveedor(int id) {
        return repositorio.buscarPorId(id);
    }

    public boolean actualizarProveedor(int id, String nombre, String telefono, String correo, String direccion) {
        Proveedor proveedor = buscarProveedor(id);

        if (proveedor == null) {
            return false;
        }

        proveedor.setNombre(nombre);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);
        proveedor.setDireccion(direccion);
        return true;
    }

    public boolean eliminarProveedor(int id) {
        Proveedor proveedor = buscarProveedor(id);

        if (proveedor == null) {
            return false;
        }

        repositorio.eliminar(proveedor);
        return true;
    }
}
