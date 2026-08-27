package repository;

import model.Proveedor;

import java.util.List;

/**
 * PRINCIPIO DIP (Dependency Inversion Principle)
 * ProveedorController ya NO depende directamente de una lista concreta
 * (ArrayList). Depende de esta abstracción. Si mañana los datos se guardan
 * en un archivo o en una base de datos, solo se crea una nueva clase que
 * implemente esta interfaz, sin tocar ProveedorController.
 */
public interface IProveedorRepository {

    void guardar(Proveedor proveedor);

    List<Proveedor> listarTodos();

    Proveedor buscarPorId(int id);

    void eliminar(Proveedor proveedor);
}
