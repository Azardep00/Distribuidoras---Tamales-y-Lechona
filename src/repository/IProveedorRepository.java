package repository;

import java.util.*;

import model.Proveedor;

public interface IProveedorRepository {
    void guardar(Proveedor p);

    List<Proveedor> listarTodos();

    Proveedor buscarPorId(int id);

    void eliminar(Proveedor p);
}
