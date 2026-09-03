package model;

import java.math.BigDecimal;

public class DetallePedido {
  private final Producto producto;
  private int cantidad;
  private final BigDecimal precioUnitario;

  public DetallePedido(Producto p, int c, BigDecimal precio) {
    if (p == null || c <= 0) throw new IllegalArgumentException("Detalle de pedido inválido.");
    producto = p;
    cantidad = c;
    precioUnitario = precio;
  }

  public Producto getProducto() {
    return producto;
  }

  public int getCantidad() {
    return cantidad;
  }

  public BigDecimal getPrecioUnitario() {
    return precioUnitario;
  }

  public void setCantidad(int v) {
    if (v <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
    cantidad = v;
  }

  public BigDecimal calcularSubtotal() {
    return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
  }
}
