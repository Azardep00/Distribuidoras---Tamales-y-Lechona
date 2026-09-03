package observer;

import model.MovimientoInventario;

public class AuditoriaInventario implements InventarioObserver {
  public void actualizar(MovimientoInventario m) {
    System.out.println(
        "AUDITORÍA | Inventario #"
            + m.getIdMovimiento()
            + " | "
            + m.getTipo()
            + " | "
            + m.getProducto().getNombre()
            + " | "
            + m.getCantidad());
  }
}
