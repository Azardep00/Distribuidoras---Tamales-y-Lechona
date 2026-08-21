package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido
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
    private Cliente cliente;
    private Empleado empleado;
    private List<DetallePedido> detalles;

    // ===== CONSTRUCTOR =====
    public Pedido(int idPedido, Cliente cliente, MetodoPago metodoPago, String direccionEntrega)
    {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.total = 0.0;
        this.pago = false;
        this.metodoPago = metodoPago;
        this.direccionEntrega = direccionEntrega;
        this.fechaEntrega = null;
        this.empleado = null;
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

    public Cliente getCliente() { return cliente; }

    public Empleado getEmpleado() { return empleado;}

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
        if (this.estado == EstadoPedido.CANCELADO || this.estado == EstadoPedido.ENTREGADO)
        {
            throw new IllegalStateException("No se puede cambiar el estado de un pedido " + this.estado);
        }
        this.estado = nuevoEstado;
    }

    public void cancelar() {
        if (this.estado == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya entregado");
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    public void confirmarEntrega() {
        if (this.estado == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede confirmar la entrega de un pedido cancelado");
        }
        this.estado = EstadoPedido.ENTREGADO;
        this.fechaEntrega = LocalDateTime.now();
    }

    public void asignarEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

}