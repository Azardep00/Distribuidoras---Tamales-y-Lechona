package model;

import java.time.LocalDateTime;

public class MovimientoInventario {
  private int idMovimiento;
  private TipoMovimiento tipo;
  private int cantidad;
  private LocalDateTime fecha;
  private String motivo;
  private Producto producto;
  private Proveedor proveedor;

  public MovimientoInventario(int id, TipoMovimiento t, int c, String m, Producto p) {
    this(id, t, c, m, p, null);
  }

  public MovimientoInventario(int id, TipoMovimiento t, int c, String m, Producto p, Proveedor pr) {
    idMovimiento = id;
    tipo = t;
    cantidad = c;
    fecha = LocalDateTime.now();
    motivo = m;
    producto = p;
    proveedor = pr;
  }

  public int getIdMovimiento() {
    return idMovimiento;
  }

  public void setIdMovimiento(int v) {
    idMovimiento = v;
  }

  public TipoMovimiento getTipo() {
    return tipo;
  }

  public int getCantidad() {
    return cantidad;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public String getMotivo() {
    return motivo;
  }

  public Producto getProducto() {
    return producto;
  }

  public Proveedor getProveedor() {
    return proveedor;
  }
}
