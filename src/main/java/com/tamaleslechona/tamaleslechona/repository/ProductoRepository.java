package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByEstadoTrue();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}