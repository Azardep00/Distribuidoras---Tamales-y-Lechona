package controller;

import model.MetodoPago;
import model.Pedido;
import observer.AuditoriaPedido;
import observer.NotificacionPedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    private static List<Pedido> pedidos = new ArrayList<>();

    public static List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public static Pedido buscarPedido(int idPedido) {
        for (Pedido pedido : pedidos) {
            if (pedido.getIdPedido() == idPedido) {
                return pedido;
            }
        }
        return null;
    }

    private static boolean existeId(int idPedido) {
        for (Pedido pedido : pedidos) {
            if (pedido.getIdPedido() == idPedido) {
                return true;
            }
        }
        return false;
    }

    public static Pedido crearPedido(int idPedido, MetodoPago metodoPago, String direccionEntrega) {
        if (existeId(idPedido)) {
            throw new RuntimeException("Error: Ya existe un pedido con ese ID");
        }

        Pedido pedido = new Pedido(idPedido, metodoPago, direccionEntrega);

        pedido.agregarObserver(new AuditoriaPedido());
        pedido.agregarObserver(new NotificacionPedido());

        pedidos.add(pedido);

        return pedido;
    }

    public static void actualizarPedido(Pedido pedido) {
        if (pedido == null) {
            return;
        }

        boolean actualizo = false;

        for (Pedido p : pedidos) {
            if (p.getIdPedido() == pedido.getIdPedido()) {
                p.actualizarPedido(pedido);
                actualizo = true;
                break;
            }
        }

        if (!actualizo) {
            System.out.println("No se encontro un pedido con ese ID");
        }
    }

    public static void eliminarPedido(int idPedido) {
        pedidos.removeIf(pedido -> pedido.getIdPedido() == idPedido);
    }
}