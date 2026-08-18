package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int idPedido;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private BigDecimal subtotal;
    private BigDecimal total;

    // Relaciones
    private Cliente cliente;
    private Empleado empleadoAsignado;
    private Entrega entrega;                 // 0..1
    private List<Detallepedido> detalles;     // 1..*  (composicion)
    private List<Pago> pagos;                 // 0..*

    public Pedido(int idPedido, LocalDateTime fecha, EstadoPedido estado, double subtotal, double total) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.estado = estado;
        this.subtotal = BigDecimal.valueOf(subtotal);
        this.total = BigDecimal.valueOf(total);
        this.detalles = new ArrayList<>();
        this.pagos = new ArrayList<>();
    }

    // ---- Getters y Setters ----
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public void setEmpleadoAsignado(Empleado empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public List<Detallepedido> getDetalles() {
        return detalles;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    // ---- Metodos del diagrama ----

    public void agregarProducto(Producto producto, int cantidad) {
        if (producto != null && producto.consultarDisponibilidad(cantidad)) {
            int idDetalle = detalles.size() + 1;
            Detallepedido detalle = new Detallepedido(idDetalle, producto, cantidad, producto.getPrecio());
            detalles.add(detalle);
            calcularTotal();
        } else {
            System.out.println("Producto no disponible en la cantidad solicitada");
        }
    }

    public void eliminarProducto(int idDetalle) {
        detalles.removeIf(d -> d.getIdDetalle() == idDetalle);
        calcularTotal();
    }

    public BigDecimal calcularTotal() {
        BigDecimal nuevoSubtotal = BigDecimal.ZERO;
        for (Detallepedido detalle : detalles) {
            nuevoSubtotal = nuevoSubtotal.add(detalle.calcularSubtotal());
        }
        this.subtotal = nuevoSubtotal;
        this.total = nuevoSubtotal; // aqui se podrian sumar domicilio, descuentos, etc.
        return this.total;
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Pedido #" + idPedido + " cambio a estado: " + nuevoEstado);
    }

    public void cancelar() {
        this.estado = EstadoPedido.CANCELADO;
        System.out.println("Pedido #" + idPedido + " cancelado");
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", subtotal=" + subtotal +
                ", total=" + total +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                '}';
    }
}