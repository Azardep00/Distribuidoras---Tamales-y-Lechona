package controller;

import model.MovimientoInventario;
import model.TipoMovimiento;
import repository.IMovimientoInventarioRepository;

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

        movimiento.setIdMovimiento(siguienteId);

        boolean resultado = movimiento.registrarMovimiento();

        if (resultado) {
            repositorio.guardar(movimiento);
            siguienteId++;
        }

        return resultado;
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
