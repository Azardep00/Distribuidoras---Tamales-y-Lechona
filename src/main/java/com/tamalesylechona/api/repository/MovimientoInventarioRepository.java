package main.java.com.tamalesylechona.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tamalesylechona.api.model.MovimientoInventario;
import com.tamalesylechona.api.model.TipoMovimiento;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProducto_IdProducto(int idProducto);

    List<MovimientoInventario> findByTipo(TipoMovimiento tipo);
}
