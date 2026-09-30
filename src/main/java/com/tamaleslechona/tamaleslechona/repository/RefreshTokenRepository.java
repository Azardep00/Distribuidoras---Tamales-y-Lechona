package com.tamaleslechona.tamaleslechona.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tamaleslechona.tamaleslechona.model.RefreshToken;
import com.tamaleslechona.tamaleslechona.model.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken r set r.revocado = true where r.usuario = :usuario and r.revocado = false")
    void revocarTodosDeUsuario(@Param("usuario") Usuario usuario);
}