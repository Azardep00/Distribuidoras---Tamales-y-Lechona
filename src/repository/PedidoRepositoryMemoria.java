package repository;

import java.util.*;
import model.Pedido;

public class PedidoRepositoryMemoria implements IPedidoRepository {
  private final List<Pedido> datos = new ArrayList<>();

  public void guardar(Pedido p) {
    datos.add(p);
  }

  public Optional<Pedido> buscarPorId(int id) {
    return datos.stream().filter(p -> p.getIdPedido() == id).findFirst();
  }

  public List<Pedido> listarTodos() {
    return new ArrayList<>(datos);
  }
}
