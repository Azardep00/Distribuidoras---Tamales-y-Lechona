package view;

import model.MovimientoInventario;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovimientoInventarioView {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void mostrarMovimientos(List<MovimientoInventario> movimientos) {
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
            return;
        }

        for (MovimientoInventario movimiento : movimientos) {
            System.out.println("ID Movimiento: " + movimiento.getIdMovimiento());
            System.out.println("Tipo: " + movimiento.getTipo());
            System.out.println("Producto: " + movimiento.getProducto().getNombre());
            System.out.println("Cantidad: " + movimiento.getCantidad());
            System.out.println("Fecha: " + movimiento.getFecha().format(FORMATO_FECHA));
            System.out.println("Motivo: " + movimiento.getMotivo());

            if (movimiento.getProveedor() != null) {
                System.out.println("Proveedor: " + movimiento.getProveedor().getNombre());
            }

            System.out.println("-----------------------------------------");
        }
    }
}
