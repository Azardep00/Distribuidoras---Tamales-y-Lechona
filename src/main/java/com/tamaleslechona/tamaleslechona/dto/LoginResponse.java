package com.tamaleslechona.tamaleslechona.dto;

import com.tamaleslechona.tamaleslechona.model.Usuario;

public record LoginResponse(
        Integer idUsuario,
        String nombre,
        String apellido,
        String correo,
        String tipoUsuario,
        String token,
        String refreshToken) {

    public static LoginResponse desde(Usuario u) {
        return new LoginResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTipoUsuario(), null, null);
    }

    public static LoginResponse desde(Usuario u, String token, String refreshToken) {
        return new LoginResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTipoUsuario(), token, refreshToken);
    }
}