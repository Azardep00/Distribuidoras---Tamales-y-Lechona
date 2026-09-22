package com.tamaleslechona.tamaleslechona.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tamaleslechona.tamaleslechona.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Un solo lugar para crear y leer los tokens. El token guarda el id, el
// correo y el tipoUsuario (Cliente/Empleado) para que el filtro de seguridad
// pueda decidir permisos sin volver a consultar la base de datos.
@Service
public class JwtService {

    // En desarrollo cae en este valor por defecto; en producción SIEMPRE
    // debe venir de una variable de entorno (igual que DB_PASSWORD).
    @Value("${jwt.secret:tamales-lechona-clave-de-desarrollo-cambiar-en-produccion-32}")
    private String secretoConfigurado;

    @Value("${jwt.expiracion-ms:86400000}") // 24 horas por defecto
    private long expiracionMs;

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(secretoConfigurado.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(String.valueOf(usuario.getIdUsuario()))
                .claim("correo", usuario.getCorreo())
                .claim("nombre", usuario.getNombre())
                .claim("tipoUsuario", usuario.getTipoUsuario())
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave())
                .compact();
    }

    // Lanza excepción (JwtException) si el token expiró, fue alterado, o el
    // formato no es válido. El filtro es quien atrapa eso.
    public Claims validarYExtraer(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
