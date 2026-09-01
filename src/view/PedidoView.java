package view;

import model.Pedido;
import java.util.List;

public class PedidoView {

    public static void mostrarPedidos(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println("ID: " + p.getIdPedido() +
                    " | Estado: " + p.getEstado() +
                    " | Pago: " + p.isPago() +
                    " | Método: " + p.getMetodoPago() +
                    " | Entrega: " + p.getDireccionEntrega());
        }
    }

    public static void mostrarPedidoActualizado() {
        System.out.println("Pedido actualizado correctamente.");
    }

    public static void mostrarPedidoEliminado() {
        System.out.println("Pedido eliminado correctamente.");
    }

    public static void mostrarPedidoNoEncontrado() {
        System.out.println("No se encontró un pedido con ese ID.");
    }
}