package repository;

import java.util.List;
import java.util.Optional;
import model.Producto;

public interface IProductoRepository {
  void guardar(Producto p);

  void actualizar(Producto p);

  Optional<Producto> buscarPorId(int id);

  List<Producto> listarTodos();

  void desactivar(int id);
}
