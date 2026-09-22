package com.tamaleslechona.tamaleslechona.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Se ejecuta en cada petición. Si trae "Authorization: Bearer <token>" válido,
// arma una Authentication con el rol correspondiente (ROLE_CLIENTE o
// ROLE_EMPLEADO) para que SecurityConfig pueda decidir con hasRole(...).
// Si no trae token, o es inválido, simplemente deja pasar la petición sin
// autenticar; es SecurityConfig quien decide si esa ruta lo necesitaba o no.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.validarYExtraer(token);
                String tipoUsuario = claims.get("tipoUsuario", String.class); // "Cliente" | "Empleado"

                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + tipoUsuario.toUpperCase()));

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException e) {
                // Token inválido/expirado: no autenticamos. La ruta protegida
                // responderá 401 más adelante en la cadena si lo requería.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
