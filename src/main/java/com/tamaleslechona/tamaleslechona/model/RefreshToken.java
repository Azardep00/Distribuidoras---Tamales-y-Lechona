package com.tamaleslechona.tamaleslechona.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// A diferencia del access token (un JWT que no se puede "borrar", solo
// esperar a que expire), este SI vive en la base de datos: se puede
// revocar de verdad cuando alguien cierra sesion, cambia su contraseña,
// o cuando detectamos que un refresh token ya usado se volvio a presentar.
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRefreshToken;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Instant fechaExpiracion;

    @Column(nullable = false)
    private boolean revocado = false;

    protected RefreshToken() {}

    public RefreshToken(String token, Usuario usuario, Instant fechaExpiracion) {
        this.token = token;
        this.usuario = usuario;
        this.fechaExpiracion = fechaExpiracion;
    }

    public Integer getIdRefreshToken() { return idRefreshToken; }
    public String getToken() { return token; }
    public Usuario getUsuario() { return usuario; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public boolean isRevocado() { return revocado; }
    public void setRevocado(boolean revocado) { this.revocado = revocado; }
}