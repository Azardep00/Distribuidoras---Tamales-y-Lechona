package com.tamaleslechona.tamaleslechona.dto;

import com.tamaleslechona.tamaleslechona.model.Usuario;

public record LoginResponse(
        Integer idUsuario,
        String nombre,
        String apellido,
        String correo,
        String tipoUsuario,
        String token) {

    // Sobrecarga sin token: la usa Cliente/Empleado al registrarse, donde no
    // hay sesión que iniciar todavía (solo devolvemos los datos creados).
    public static LoginResponse desde(Usuario u) {
        return new LoginResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTipoUsuario(), null);
    }

    public static LoginResponse desde(Usuario u, String token) {
        return new LoginResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTipoUsuario(), token);
    }
}
