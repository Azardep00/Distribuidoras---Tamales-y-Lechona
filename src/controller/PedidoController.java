package controller;

import model.*;
import observer.AuditoriaPedido;
import observer.NotificacionPedido;

import java.util.*;

public class PedidoController {

    private static final List<Pedido> pedidos = new ArrayList<>();
    private static int siguienteId = 1;

    public static List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public static Pedido buscarPedido(int id) {
        return pedidos.stream()
                .filter(p -> p.getIdPedido() == id)
                .findFirst()
                .orElse(null);
    }

    public static Pedido crearPedido(MetodoPago metodo, String direccion) {
        if (metodo == null) {
            throw new IllegalArgumentException("Selecciona un método de pago.");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección de entrega es obligatoria.");
        }

        Pedido p = new Pedido(siguienteId++, metodo, direccion.trim());
        p.agregarObserver(new AuditoriaPedido());
        p.agregarObserver(new NotificacionPedido());
        pedidos.add(p);
        return p;
    }

    /** Compatibilidad con la versión anterior. El ID manual ya no forma parte del flujo de UI. */
    public static Pedido crearPedido(int id, MetodoPago metodo, String direccion) {
        if (buscarPedido(id) != null) {
            throw new IllegalArgumentException("Ya existe un pedido con ese ID.");
        }

        Pedido p = new Pedido(id, metodo, direccion);
        p.agregarObserver(new AuditoriaPedido());
        p.agregarObserver(new NotificacionPedido());
        pedidos.add(p);
        siguienteId = Math.max(siguienteId, id + 1);
        return p;
    }

    public static boolean actualizarPedido(Pedido pedido) {
        Pedido actual = buscarPedido(pedido.getIdPedido());
        if (actual == null) {
            return false;
        }
        actual.actualizarPedido(pedido);
        return true;
    }

    public static boolean eliminarPedido(int id) {
        return pedidos.removeIf(p -> p.getIdPedido() == id);
    }
}