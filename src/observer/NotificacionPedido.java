package observer;
import model.Pedido;

public class NotificacionPedido implements PedidoObserver {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.println(
                "NOTIFICACIÓN: Se avisó al cliente sobre el pedido #" +
                        pedido.getIdPedido() + " (" + pedido.getEstado() + ")"
        );
    }
}