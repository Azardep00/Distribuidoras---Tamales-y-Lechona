package observer;

import model.MovimientoInventario;

public interface InventarioObserver {
    void actualizar(MovimientoInventario movimiento);
}
