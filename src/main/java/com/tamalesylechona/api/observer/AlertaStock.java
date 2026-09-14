package main.java.com.tamalesylechona.api.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tamalesylechona.api.model.MovimientoInventario;

@Component
public class AlertaStock implements InventarioObserver {

    private static final Logger log = LoggerFactory.getLogger(AlertaStock.class);

    // Configurable en application.properties como inventario.stock-minimo
    // (en el proyecto original era un valor fijo: new AlertaStock(5)).
    @Value("${inventario.stock-minimo:5}")
    private int minimo;

    @Override
    public void actualizar(MovimientoInventario m) {
        if (m.getProducto().getStock() <= minimo) {
            log.warn(
                    "ALERTA | Stock bajo de {}: quedan {} unidades",
                    m.getProducto().getNombre(),
                    m.getProducto().getStock());
        }
    }
}
