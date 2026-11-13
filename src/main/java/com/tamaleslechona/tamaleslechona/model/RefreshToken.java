package com.tamaleslechona.tamaleslechona.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

// Vive en la base de datos para poder revocarse de verdad (logout, cambio de
// contraseña o reutilización detectada).
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String idRefreshToken;

    @Indexed(unique = true)
    private String token;

    @Indexed
    private String idUsuario;

    private Instant fechaExpiracion;

    private boolean revocado = false;

    protected RefreshToken() {}

    public RefreshToken(String token, String idUsuario, Instant fechaExpiracion) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getIdRefreshToken() { return idRefreshToken; }
    public String getToken() { return token; }
    public String getIdUsuario() { return idUsuario; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public boolean isRevocado() { return revocado; }
    public void setRevocado(boolean revocado) { this.revocado = revocado; }
}