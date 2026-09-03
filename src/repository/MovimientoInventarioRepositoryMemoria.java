package repository;

import java.util.*;
import model.*;

public class MovimientoInventarioRepositoryMemoria implements IMovimientoInventarioRepository {
  private final List<MovimientoInventario> datos = new ArrayList<>();

  public void guardar(MovimientoInventario m) {
    datos.add(m);
  }

  public List<MovimientoInventario> listarTodos() {
    return new ArrayList<>(datos);
  }

  public MovimientoInventario buscarPorId(int id) {
    return datos.stream().filter(m -> m.getIdMovimiento() == id).findFirst().orElse(null);
  }

  public List<MovimientoInventario> listarPorProducto(int id) {
    return datos.stream()
        .filter(m -> m.getProducto() != null && m.getProducto().getIdProducto() == id)
        .toList();
  }

  public List<MovimientoInventario> listarPorTipo(TipoMovimiento t) {
    return datos.stream().filter(m -> m.getTipo() == t).toList();
  }
}
