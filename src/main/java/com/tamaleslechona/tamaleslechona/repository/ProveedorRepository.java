package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.Proveedor;

public interface ProveedorRepository extends MongoRepository<Proveedor, String> {

    List<Proveedor> findByEstadoTrue();

    List<Proveedor> findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCase(
            String nombre, String telefono, String correo);
}
