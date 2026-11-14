package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.Producto;

// MongoRepository ya trae save(), findById(), findAll(), deleteById(), etc.
// Solo agregamos lo que no viene por defecto.
public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByEstadoTrue();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
