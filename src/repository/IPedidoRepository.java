package repository;

import java.util.*;
import model.Pedido;

public interface IPedidoRepository {
  void guardar(Pedido p);

  Optional<Pedido> buscarPorId(int id);

  List<Pedido> listarTodos();
}
