package com.tamaleslechona.tamaleslechona.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tamaleslechona.tamaleslechona.exception.CredencialesInvalidasException;
import com.tamaleslechona.tamaleslechona.model.RefreshToken;
import com.tamaleslechona.tamaleslechona.model.Usuario;
import com.tamaleslechona.tamaleslechona.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final SecureRandom random = new SecureRandom();

    @Value("${refresh.expiracion-dias:30}")
    private long expiracionDias;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken crear(Usuario usuario) {
        String valor = generarValorAleatorio();
        Instant expira = Instant.now().plus(expiracionDias, ChronoUnit.DAYS);
        return repo.save(new RefreshToken(valor, usuario, expira));
    }

    // Valida el refresh token y, si es correcto, lo ROTA: el viejo queda
    // invalido y se crea uno nuevo. Si alguien presenta uno YA revocado,
    // es señal de que un token viejo anda circulando donde no debia; en
    // ese caso se cierra la sesion en TODOS los dispositivos del usuario.
    public RefreshToken validarYRotar(String valorRecibido) {
        RefreshToken existente = repo.findByToken(valorRecibido)
                .orElseThrow(() -> new CredencialesInvalidasException("Sesión inválida, inicia sesión de nuevo."));

        if (existente.isRevocado()) {
            repo.revocarTodosDeUsuario(existente.getUsuario());
            throw new CredencialesInvalidasException(
                    "Se detectó un uso inválido de la sesión. Por seguridad, inicia sesión de nuevo.");
        }

        if (Instant.now().isAfter(existente.getFechaExpiracion())) {
            throw new CredencialesInvalidasException("La sesión expiró, inicia sesión de nuevo.");
        }

        existente.setRevocado(true);
        repo.save(existente);

        return crear(existente.getUsuario());
    }

    public void revocar(String valor) {
        repo.findByToken(valor).ifPresent(rt -> {
            rt.setRevocado(true);
            repo.save(rt);
        });
    }

    public void revocarTodosDeUsuario(Usuario usuario) {
        repo.revocarTodosDeUsuario(usuario);
    }

    private String generarValorAleatorio() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}