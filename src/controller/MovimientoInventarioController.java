package controller;

import model.MovimientoInventario;
import model.TipoMovimiento;
import repository.IMovimientoInventarioRepository;
import model.Producto;

import java.util.List;

/**
 * PRINCIPIO SRP: ya no imprime en consola (eso lo hace view.MovimientoInventarioView).
 * PRINCIPIO DIP: depende de IMovimientoInventarioRepository, no de un ArrayList concreto.
 */
public class MovimientoInventarioController {

    private final IMovimientoInventarioRepository repositorio;
    private int siguienteId;

    public MovimientoInventarioController(IMovimientoInventarioRepository repositorio) {
        this.repositorio = repositorio;
        this.siguienteId = 1;
    }

    public boolean registrarMovimiento(MovimientoInventario movimiento) {

        if (movimiento == null || movimiento.getProducto() == null) {
            return false;
        }

        Producto producto = movimiento.getProducto();
        int cantidad = movimiento.getCantidad();

        if (cantidad <= 0) {
            return false;
        }

        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {

            producto.setStock(producto.getStock() + cantidad);

        } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {

            if (!producto.consultarDisponibilidad(cantidad)) {
                return false;
            }

            producto.setStock(producto.getStock() - cantidad);

        } else {
            return false;
        }

        movimiento.setIdMovimiento(siguienteId);
        repositorio.guardar(movimiento);
        siguienteId++;

        return true;
    }

    public List<MovimientoInventario> listarMovimientos() {
        return repositorio.listarTodos();
    }

    public MovimientoInventario buscarMovimiento(int idMovimiento) {
        return repositorio.buscarPorId(idMovimiento);
    }

    public List<MovimientoInventario> listarMovimientosPorProducto(int idProducto) {
        return repositorio.listarPorProducto(idProducto);
    }

    public List<MovimientoInventario> listarMovimientosPorTipo(TipoMovimiento tipo) {
        return repositorio.listarPorTipo(tipo);
    }
}
