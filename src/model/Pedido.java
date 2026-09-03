package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import observer.PedidoObserver;

public class Pedido implements ActualizablePedido {
  private final int idPedido;
  private final LocalDateTime fecha;
  private EstadoPedido estado;
  private BigDecimal total = BigDecimal.ZERO;
  private boolean pago;
  private MetodoPago metodoPago;
  private String direccionEntrega;
  private LocalDateTime fechaEntrega;
  private final Cliente cliente;
  private boolean inventarioDescontado;
  private final List<DetallePedido> detalles = new ArrayList<>();
  private final List<PedidoObserver> observadores = new ArrayList<>();

  public Pedido(int id, Cliente cliente, MetodoPago metodo, String direccion) {
    if (cliente == null) throw new IllegalArgumentException("El cliente es obligatorio.");
    if (metodo == null) throw new IllegalArgumentException("El método de pago es obligatorio.");
    if (direccion == null || direccion.isBlank())
      throw new IllegalArgumentException("La dirección de entrega es obligatoria.");
    idPedido = id;
    this.cliente = cliente;
    metodoPago = metodo;
    direccionEntrega = direccion.trim();
    fecha = LocalDateTime.now();
    estado = EstadoPedido.PENDIENTE;
  }

  public int getIdPedido() {
    return idPedido;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public EstadoPedido getEstado() {
    return estado;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public boolean isPago() {
    return pago;
  }

  public MetodoPago getMetodoPago() {
    return metodoPago;
  }

  public String getDireccionEntrega() {
    return direccionEntrega;
  }

  public LocalDateTime getFechaEntrega() {
    return fechaEntrega;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public boolean isInventarioDescontado() {
    return inventarioDescontado;
  }

  public void setInventarioDescontado(boolean v) {
    inventarioDescontado = v;
  }

  public List<DetallePedido> getDetalles() {
    return new ArrayList<>(detalles);
  }

  public void setPago(boolean v) {
    pago = v;
    if (v && estado == EstadoPedido.CONFIRMADO) cambiarEstado(EstadoPedido.PAGADO);
  }

  public void setMetodoPago(MetodoPago v) {
    metodoPago = v;
  }

  public void setDireccionEntrega(String v) {
    direccionEntrega = v;
  }

  public void agregarDetalle(DetallePedido d) {
    if (d == null) throw new IllegalArgumentException("Detalle inválido.");
    detalles.add(d);
    calcularTotal();
  }

  public void eliminarDetalle(DetallePedido d) {
    detalles.remove(d);
    calcularTotal();
  }

  public BigDecimal calcularTotal() {
    total =
        detalles.stream()
            .map(DetallePedido::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return total;
  }

  public void cambiarEstado(EstadoPedido nuevo) {
    if (nuevo == null || estado == EstadoPedido.ENTREGADO || estado == EstadoPedido.CANCELADO)
      throw new IllegalStateException("El pedido no admite más cambios de estado.");
    if (!transicionValida(estado, nuevo))
      throw new IllegalStateException("No se puede pasar de " + estado + " a " + nuevo + ".");
    estado = nuevo;
    if (nuevo == EstadoPedido.ENTREGADO) fechaEntrega = LocalDateTime.now();
    notificar();
  }

  private boolean transicionValida(EstadoPedido a, EstadoPedido b) {
    return switch (a) {
      case PENDIENTE -> b == EstadoPedido.CONFIRMADO || b == EstadoPedido.CANCELADO;
      case CONFIRMADO ->
          b == EstadoPedido.PAGADO
              || b == EstadoPedido.EN_PREPARACION
              || b == EstadoPedido.CANCELADO;
      case PAGADO -> b == EstadoPedido.EN_PREPARACION || b == EstadoPedido.CANCELADO;
      case EN_PREPARACION -> b == EstadoPedido.LISTO || b == EstadoPedido.CANCELADO;
      case LISTO -> b == EstadoPedido.ENTREGADO || b == EstadoPedido.CANCELADO;
      default -> false;
    };
  }

  public void cancelar() {
    if (estado == EstadoPedido.CANCELADO || estado == EstadoPedido.ENTREGADO)
      throw new IllegalStateException("El pedido no se puede cancelar.");
    estado = EstadoPedido.CANCELADO;
    notificar();
  }

  public void actualizarPedido(Pedido p) {
    setMetodoPago(p.metodoPago);
    setDireccionEntrega(p.direccionEntrega);
  }

  public void agregarObserver(PedidoObserver o) {
    if (o != null && !observadores.contains(o)) observadores.add(o);
  }

  public void removerObserver(PedidoObserver o) {
    observadores.remove(o);
  }

  private void notificar() {
    for (PedidoObserver o : observadores) o.actualizar(this);
  }
}
