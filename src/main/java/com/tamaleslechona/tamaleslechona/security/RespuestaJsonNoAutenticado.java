package com.tamaleslechona.tamaleslechona.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamaleslechona.tamaleslechona.exception.GlobalExceptionHandler.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Se dispara cuando la ruta requiere token y no llegó ninguno (o llegó
// inválido). Responde igual que cualquier otro error de la API: un JSON
// { "mensaje": "..." } con 401, no la página HTML por defecto de Spring.
public class RespuestaJsonNoAutenticado implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                mapper.writeValueAsString(new ErrorResponse("Debes iniciar sesión para acceder a este recurso.")));
    }
}
