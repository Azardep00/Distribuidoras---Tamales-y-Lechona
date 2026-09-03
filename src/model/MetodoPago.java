package model;

public enum MetodoPago {
  EFECTIVO("Efectivo"),
  TARJETA("Tarjeta"),
  TRANSFERENCIA("Transferencia"),
  WOMPI("Wompi");
  private final String etiqueta;

  MetodoPago(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
