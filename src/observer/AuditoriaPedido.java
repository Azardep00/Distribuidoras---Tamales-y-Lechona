package observer;

import model.Pedido;

public class AuditoriaPedido implements PedidoObserver {
  public void actualizar(Pedido p) {
    System.out.println("AUDITORÍA | Pedido #" + p.getIdPedido() + " -> " + p.getEstado());
  }
}
