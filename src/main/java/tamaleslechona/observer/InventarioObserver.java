package tamaleslechona.observer;

import tamaleslechona.model.MovimientoInventario;

public interface InventarioObserver {
    void actualizar(MovimientoInventario movimiento);
}
