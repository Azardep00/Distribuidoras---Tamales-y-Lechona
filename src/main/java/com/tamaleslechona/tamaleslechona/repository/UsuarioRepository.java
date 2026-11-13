package com.tamaleslechona.tamaleslechona.repository;

import java.util.List;
import java.util.Optional;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    List<Usuario> findByEstadoTrue();
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
}