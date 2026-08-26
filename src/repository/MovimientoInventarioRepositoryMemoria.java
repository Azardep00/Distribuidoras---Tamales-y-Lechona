package repository;

import model.MovimientoInventario;
import model.TipoMovimiento;

import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioRepositoryMemoria implements IMovimientoInventarioRepository {

    private List<MovimientoInventario> movimientos;

    public MovimientoInventarioRepositoryMemoria() {
        this.movimientos = new ArrayList<>();
    }

    @Override
    public void guardar(MovimientoInventario movimiento) {
        movimientos.add(movimiento);
    }

    @Override
    public List<MovimientoInventario> listarTodos() {
        return movimientos;
    }

    @Override
    public MovimientoInventario buscarPorId(int idMovimiento) {
        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getIdMovimiento() == idMovimiento) {
                return movimiento;
            }
        }
        return null;
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(int idProducto) {
        List<MovimientoInventario> resultado = new ArrayList<>();
        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getProducto().getIdProducto() == idProducto) {
                resultado.add(movimiento);
            }
        }
        return resultado;
    }

    @Override
    public List<MovimientoInventario> listarPorTipo(TipoMovimiento tipo) {
        List<MovimientoInventario> resultado = new ArrayList<>();
        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getTipo() == tipo) {
                resultado.add(movimiento);
            }
        }
        return resultado;
    }
}
