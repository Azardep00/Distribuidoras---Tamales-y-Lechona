package repository;

import java.util.*;
import model.*;

public interface IMovimientoInventarioRepository {
  void guardar(MovimientoInventario m);

  List<MovimientoInventario> listarTodos();

  MovimientoInventario buscarPorId(int id);

  List<MovimientoInventario> listarPorProducto(int idProducto);

  List<MovimientoInventario> listarPorTipo(TipoMovimiento tipo);
}
