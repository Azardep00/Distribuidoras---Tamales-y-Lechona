package model;

import java.math.BigDecimal;

public class Tamal extends Producto implements IActualizableProducto {
  private TipoTamal tipo;
  private TamanoTamal tamaño;

  public Tamal(
      int id, String n, String d, BigDecimal p, int s, boolean e, TipoTamal t, TamanoTamal z) {
    super(id, n, d, p, s, e);
    tipo = t;
    tamaño = z;
  }

  public TipoTamal getTipo() {
    return tipo;
  }

  public void setTipo(TipoTamal v) {
    tipo = v;
  }

  public TamanoTamal getTamaño() {
    return tamaño;
  }

  public void setTamaño(TamanoTamal v) {
    tamaño = v;
  }

  public void actualizarDatos(Producto p) {
    if (p instanceof Tamal t) {
      tipo = t.tipo;
      tamaño = t.tamaño;
    }
  }

  public BigDecimal calcularPrecio() {
    return getPrecio();
  }

  public String getTipoProducto() {
    return "Tamal";
  }

  public String getDetalleEspecifico() {
    return tipo + " · " + tamaño;
  }
}
