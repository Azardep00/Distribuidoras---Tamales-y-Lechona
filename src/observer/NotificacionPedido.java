package observer;

import model.Pedido;

public class NotificacionPedido implements PedidoObserver {
  public void actualizar(Pedido p) {
    System.out.println(
        "NOTIFICACIÓN | Pedido #" + p.getIdPedido() + " actualizado: " + p.getEstado());
  }
}
