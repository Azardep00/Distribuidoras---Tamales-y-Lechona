package repository;

import model.MovimientoInventario;
import model.TipoMovimiento;

import java.util.List;

public interface IMovimientoInventarioRepository {

    void guardar(MovimientoInventario movimiento);

    List<MovimientoInventario> listarTodos();

    MovimientoInventario buscarPorId(int idMovimiento);

    List<MovimientoInventario> listarPorProducto(int idProducto);

    List<MovimientoInventario> listarPorTipo(TipoMovimiento tipo);
}
