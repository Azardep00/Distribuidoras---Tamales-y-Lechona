package controller;

import model.MovimientoInventario;
import model.TipoMovimiento;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MovimientoInventarioController {

    private ArrayList<MovimientoInventario> movimientos;
    private int siguienteId;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public MovimientoInventarioController() {
        movimientos = new ArrayList<>();
        siguienteId = 1;
    }

    public boolean registrarMovimiento(MovimientoInventario movimiento) {

        movimiento.setIdMovimiento(siguienteId);

        boolean resultado = movimiento.registrarMovimiento();

        if (resultado) {
            movimientos.add(movimiento);
            siguienteId++;
        }

        return resultado;
    }

    public void mostrarMovimientos() {

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

    public MovimientoInventario buscarMovimiento(int idMovimiento) {

        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getIdMovimiento() == idMovimiento) {
                return movimiento;
            }
        }

        return null;
    }

    public ArrayList<MovimientoInventario> listarMovimientosPorProducto(int idProducto) {

        ArrayList<MovimientoInventario> resultado = new ArrayList<>();

        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getProducto().getIdProducto() == idProducto) {
                resultado.add(movimiento);
            }
        }

        return resultado;
    }

    public ArrayList<MovimientoInventario> listarMovimientosPorTipo(TipoMovimiento tipo) {

        ArrayList<MovimientoInventario> resultado = new ArrayList<>();

        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getTipo() == tipo) {
                resultado.add(movimiento);
            }
        }

        return resultado;
    }

    public ArrayList<MovimientoInventario> getMovimientos() {
        return movimientos;
    }
}