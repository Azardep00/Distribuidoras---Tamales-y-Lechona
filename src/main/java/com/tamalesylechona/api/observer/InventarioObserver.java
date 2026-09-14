package main.java.com.tamalesylechona.api.observer;

import com.tamalesylechona.api.model.MovimientoInventario;

public interface InventarioObserver {
    void actualizar(MovimientoInventario movimiento);
}
