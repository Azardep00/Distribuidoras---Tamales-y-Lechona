package main.java.com.tamalesylechona.api.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tamalesylechona.api.model.MovimientoInventario;

// @Component: Spring lo detecta solo y lo agrega automáticamente a la lista
// de observadores que recibe MovimientoInventarioService (ver más abajo).
@Component
public class AuditoriaInventario implements InventarioObserver {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaInventario.class);

    @Override
    public void actualizar(MovimientoInventario m) {
        log.info(
                "AUDITORÍA | Inventario #{} | {} | {} | cantidad {}",
                m.getIdMovimiento(),
                m.getTipo(),
                m.getProducto().getNombre(),
                m.getCantidad());
    }
}
