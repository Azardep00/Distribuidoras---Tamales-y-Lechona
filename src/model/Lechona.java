package model;

import java.math.BigDecimal;

public class Lechona extends Producto implements IActualizableProducto {
  private TamanoLechona tamaño;
  private int numeroPorciones;

  public Lechona(
      int id, String n, String d, BigDecimal p, int s, boolean e, TamanoLechona z, int por) {
    super(id, n, d, p, s, e);
    tamaño = z;
    numeroPorciones = por;
  }

  public TamanoLechona getTamaño() {
    return tamaño;
  }

  public void setTamaño(TamanoLechona v) {
    tamaño = v;
  }

  public int getNumeroPorciones() {
    return numeroPorciones;
  }

  public void setNumeroPorciones(int v) {
    numeroPorciones = v;
  }

  public void actualizarDatos(Producto p) {
    if (p instanceof Lechona l) {
      tamaño = l.tamaño;
      numeroPorciones = l.numeroPorciones;
    }
  }

  public BigDecimal calcularPrecio() {
    return getPrecio();
  }

  public String getTipoProducto() {
    return "Lechona";
  }

  public String getDetalleEspecifico() {
    return tamaño + " · " + numeroPorciones + " porciones";
  }
}
