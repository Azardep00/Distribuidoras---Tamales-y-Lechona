package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tamaleslechona.tamaleslechona.model.MovimientoInventario;
import com.tamaleslechona.tamaleslechona.model.TipoMovimiento;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProducto_IdProducto(int idProducto);

    List<MovimientoInventario> findByTipo(TipoMovimiento tipo);
}
