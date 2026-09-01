package observer;
import model.Pedido;

public interface PedidoObserver {
    void actualizar(Pedido pedido);
}