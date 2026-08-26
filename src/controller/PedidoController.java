package controller;

import model.Pedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    private static List<Pedido> pedidos = new ArrayList<>();


    public static void agregarPedido(Pedido pedido) {
        if (pedido != null) {
            if (!existeId(pedido.getIdPedido())) {
                pedidos.add(pedido);
                System.out.println("Pedido agregado correctamente");
            }
            else
            {
                throw new RuntimeException("Error: Ya existe un pedido con ese ID");
            }
        }
        else
        {
            throw new RuntimeException("Error: pedido nulo");
        }
    }

    public static List<Pedido> listarPedidos() {
        return pedidos;
    }

    public static Pedido buscarPedido(int idPedido)
    {
        for (Pedido pedido : pedidos) {
            if (pedido.getIdPedido() == idPedido)
            {
                return pedido;
            }
        }
        return null;
    }

    private static boolean existeId(int idPedido)
    {
        for (Pedido pedido : pedidos) {
            if (pedido.getIdPedido() == idPedido)
            {
                return true;
            }
        }
        return false;
    }

    public static void actualizarPedido(Pedido pedido)
    {
        boolean actualizo = false;

        for (Pedido p : pedidos) {
            if (p.getIdPedido() == pedido.getIdPedido()) {

                p.actualizarPedido(pedido);

                actualizo = true;
                System.out.println("Pedido actualizado");
                break;
            }
        }

        if (!actualizo) {
            System.out.println("No se encontro un pedido con ese ID");
        }
    }

    public static void eliminarPedido(int idPedido) {
        for (Pedido pedido : pedidos)
        {
            if (pedido.getIdPedido() == idPedido)
            {
                pedidos.remove(pedido);
                System.out.println("Pedido eliminado correctamente");
                return;
            }
        }

        System.out.println("No se encontro ningun pedido con ese ID");
    }
}