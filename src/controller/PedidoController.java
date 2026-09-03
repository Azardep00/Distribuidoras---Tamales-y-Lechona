package controller;

import java.util.*;

import model.*;
import observer.*;
import repository.IPedidoRepository;

public class PedidoController {
    private final IPedidoRepository repo;
    private final MovimientoInventarioController inventario;
    private int siguienteId = 1;

    public PedidoController(IPedidoRepository repo, MovimientoInventarioController inventario) {
        this.repo = repo;
        this.inventario = inventario;
    }

    public Pedido crearPedido(
            Cliente cliente, MetodoPago metodo, String direccion, List<DetallePedido> detalles) {
        Pedido p = new Pedido(siguienteId++, cliente, metodo, direccion);
        if (detalles == null || detalles.isEmpty())
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        for (DetallePedido d : detalles) {
            if (!d.getProducto().consultarDisponibilidad(d.getCantidad()))
                throw new IllegalArgumentException(
                        "Stock insuficiente para " + d.getProducto().getNombre() + ".");
            p.agregarDetalle(d);
        }
        p.agregarObserver(new AuditoriaPedido());
        p.agregarObserver(new NotificacionPedido());
        repo.guardar(p);
        cliente.agregarPedido(p);
        return p;
    }

    public void confirmar(Pedido p) {
        requiere(p);
        if (p.getTotal().signum() <= 0) throw new IllegalStateException("El pedido no tiene valor.");
        if (p.isInventarioDescontado()) return;
        for (DetallePedido d : p.getDetalles())
            if (!d.getProducto().consultarDisponibilidad(d.getCantidad()))
                throw new IllegalStateException(
                        "Stock insuficiente para " + d.getProducto().getNombre() + " al confirmar el pedido.");
        for (DetallePedido d : p.getDetalles())
            inventario.registrarSalida(
                    d.getProducto(), d.getCantidad(), "Reserva por pedido #" + p.getIdPedido());
        p.setInventarioDescontado(true);
        p.cambiarEstado(EstadoPedido.CONFIRMADO);
    }

    public void registrarPago(Pedido p) {
        requiere(p);
        if (!p.isInventarioDescontado()) confirmar(p);
        if (p.isPago()) return;
        p.setPago(true);
        if (p.getEstado() == EstadoPedido.CONFIRMADO) p.cambiarEstado(EstadoPedido.PAGADO);
    }

    public void avanzarEstado(Pedido p, EstadoPedido nuevo) {
        requiere(p);
        p.cambiarEstado(nuevo);
    }

    public void cancelar(Pedido p) {
        requiere(p);
        if (p.isPago())
            throw new IllegalStateException(
                    "Un pedido pagado no puede cancelarse desde esta operación; requiere gestionar un reembolso.");
        if (p.isInventarioDescontado()) {
            for (DetallePedido d : p.getDetalles())
                inventario.registrarReversion(
                        d.getProducto(),
                        d.getCantidad(),
                        "Reversión por cancelación pedido #" + p.getIdPedido());
            p.setInventarioDescontado(false);
        }
        p.cancelar();
    }

    private void requiere(Pedido p) {
        if (p == null) throw new IllegalArgumentException("Pedido inválido.");
    }

    public List<Pedido> listar() {
        return repo.listarTodos();
    }

    public Pedido buscar(int id) {
        return repo.buscarPorId(id).orElse(null);
    }

    public long pendientes() {
        return listar().stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
    }
}
