package model;

public enum EstadoPedido {
  PENDIENTE("Pendiente"),
  CONFIRMADO("Confirmado"),
  PAGADO("Pagado"),
  EN_PREPARACION("En preparación"),
  LISTO("Listo"),
  ENTREGADO("Entregado"),
  CANCELADO("Cancelado");

  private final String etiqueta;

  EstadoPedido(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  public String getEtiqueta() {
    return etiqueta;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
