import controller.ProductoController;
import model.*;
import view.GUIProducto;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        ProductoController productoController = new ProductoController();
        GUIProducto guiProducto = new GUIProducto();

        Tamal tamal1 = new Tamal(1, "Tamal Tolimense", "Tamal tradicional envuelto en hoja de plátano",
                new BigDecimal("8000"), 20, true, TipoTamal.TOLIMENSE, TamañoTamal.GRANDE);

        Lechona lechona1 = new Lechona(2, "Lechona Tolimense", "Lechona rellena de arroz y arveja",
                new BigDecimal("25000"), 10, true, TamañoLechona.MEDIANA, 8);

        ProductoController.agregarProducto(tamal1);
        ProductoController.agregarProducto(lechona1);

        System.out.println(productoController.listarProductos());
    }
}