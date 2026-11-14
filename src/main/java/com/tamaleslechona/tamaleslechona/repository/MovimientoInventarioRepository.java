package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.MovimientoInventario;
import com.tamaleslechona.tamaleslechona.model.TipoMovimiento;

public interface MovimientoInventarioRepository extends MongoRepository<MovimientoInventario, String> {

    List<MovimientoInventario> findByIdProducto(String idProducto);

    List<MovimientoInventario> findByTipo(TipoMovimiento tipo);

    // Para decidir si un proveedor se puede borrar de verdad.
    boolean existsByIdProveedor(String idProveedor);
}
