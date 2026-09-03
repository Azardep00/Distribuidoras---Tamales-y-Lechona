package observer;

import model.MovimientoInventario;

public class AlertaStock implements InventarioObserver {
  private final int minimo;

  public AlertaStock(int minimo) {
    this.minimo = minimo;
  }

  public void actualizar(MovimientoInventario m) {
    if (m.getProducto().getStock() <= minimo)
      System.out.println(
          "ALERTA | Stock bajo de "
              + m.getProducto().getNombre()
              + ": "
              + m.getProducto().getStock());
  }
}
