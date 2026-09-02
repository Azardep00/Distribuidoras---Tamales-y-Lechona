package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import observer.PedidoObserver;

/**
 * PRINCIPIO ISP: Pedido implementa ActualizablePedido, que antes existía
 * en el proyecto pero ninguna clase la usaba (interfaz "muerta").
 * Ahora Pedido es responsable de saber actualizar sus propios datos editables,
 * en vez de que PedidoController le "meta la mano" copiando campos uno por uno.
 */
public class Pedido implements ActualizablePedido
{

    // ===== ATRIBUTOS =====
    private int idPedido;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private double total;
    private boolean pago;
    private MetodoPago metodoPago;
    private String direccionEntrega;
    private LocalDateTime fechaEntrega;

    private List<DetallePedido> detalles;

    // ===== CONSTRUCTOR =====
    public Pedido(int idPedido, MetodoPago metodoPago, String direccionEntrega)
    {
        this.idPedido = idPedido;

        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.total = 0.0;
        this.pago = false;
        this.metodoPago = metodoPago;
        this.direccionEntrega = direccionEntrega;
        this.fechaEntrega = null;
        this.detalles = new ArrayList<>();
    }

    // ===== GETTERS AND SETTERS =====
    public int getIdPedido() { return idPedido; }

    public LocalDateTime getFecha() { return fecha; }

    public EstadoPedido getEstado() { return estado; }

    public double getTotal() { return total; }

    public boolean isPago() { return pago; }

    public MetodoPago getMetodoPago() { return metodoPago; }

    public String getDireccionEntrega() { return direccionEntrega; }


    public LocalDateTime getFechaEntrega() { return fechaEntrega; }


    public List<DetallePedido> getDetalles() { return new ArrayList<>(detalles); }

    public void setPago(boolean pago) { this.pago = pago; }

    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    // ===== MÉTODOS =====

    public double calcularTotal()
    {
        this.total = 0.0;
        for (DetallePedido d : detalles) {
            this.total += d.calcularSubtotal();
        }
        return this.total;
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {

        if (this.estado == EstadoPedido.CANCELADO ||
                this.estado == EstadoPedido.ENTREGADO) {
            return;
        }

        if (nuevoEstado == null) {
            return;
        }

        this.estado = nuevoEstado;
        notificarObservadores();
    }

    public void cancelar() {
        if (this.estado == EstadoPedido.ENTREGADO ||
                this.estado == EstadoPedido.CANCELADO) {
            return;
        }

        this.estado = EstadoPedido.CANCELADO;
        notificarObservadores();
    }

    public void confirmarEntrega() {

        if (this.estado == EstadoPedido.CANCELADO ||
                this.estado == EstadoPedido.ENTREGADO) {
            return;
        }

        this.estado = EstadoPedido.ENTREGADO;
        this.fechaEntrega = LocalDateTime.now();

        notificarObservadores();
    }

    @Override
    public void actualizarPedido(Pedido pedido) {
        if (pedido == null) {
            return;
        }

        this.pago = pedido.isPago();
        this.metodoPago = pedido.getMetodoPago();
        this.direccionEntrega = pedido.getDireccionEntrega();
    }

    private List<PedidoObserver> observadores = new ArrayList<>();

    public void agregarObserver(PedidoObserver observer) {
        observadores.add(observer);
    }

    public void removerObserver(PedidoObserver observer) {
        observadores.remove(observer);
    }

    private void notificarObservadores() {
        for (PedidoObserver observer : observadores) {
            observer.actualizar(this);
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        if (detalle == null) {
            return;
        }

        detalles.add(detalle);
        calcularTotal();
    }

    public void eliminarDetalle(DetallePedido detalle) {
        if (detalle == null) {
            return;
        }

        detalles.remove(detalle);
        calcularTotal();
    }

}