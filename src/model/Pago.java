package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {

    private int idPago;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private MetodoPago metodo;
    private EstadoPago estado;

    private Pedido pedido;

    public Pago(int idPago, LocalDateTime fecha, BigDecimal monto, MetodoPago metodo, EstadoPago estado) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
    }

    // ---- Getters y Setters ----
    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    // ---- Metodos del diagrama ----

    public void procesarPago() {
        this.estado = EstadoPago.PENDIENTE;
        System.out.println("Procesando pago #" + idPago + " por " + monto);
    }

    public void confirmarPago() {
        this.estado = EstadoPago.CONFIRMADO;
        System.out.println("Pago #" + idPago + " confirmado");
    }

    public void cancelarPago() {
        this.estado = EstadoPago.CANCELADO;
        System.out.println("Pago #" + idPago + " cancelado");
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", metodo=" + metodo +
                ", estado=" + estado +
                '}';
    }
}