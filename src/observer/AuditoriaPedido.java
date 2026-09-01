package observer;
import model.Pedido;

public class AuditoriaPedido implements PedidoObserver {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.println(
                "AUDITORÍA: Pedido #" + pedido.getIdPedido() +
                        " cambió a estado " + pedido.getEstado()
        );
    }
}