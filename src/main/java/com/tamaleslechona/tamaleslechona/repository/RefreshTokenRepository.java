package com.tamaleslechona.tamaleslechona.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.tamaleslechona.tamaleslechona.model.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    @Query("{ 'idUsuario': ?0, 'revocado': false }")
    @Update("{ '$set': { 'revocado': true } }")
    void revocarTodosDeUsuario(String idUsuario);
}