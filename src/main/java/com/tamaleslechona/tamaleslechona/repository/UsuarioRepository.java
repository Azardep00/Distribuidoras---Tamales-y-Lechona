package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tamaleslechona.tamaleslechona.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    List<Usuario> findByEstadoTrue();

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);
}
