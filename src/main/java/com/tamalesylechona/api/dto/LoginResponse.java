package main.java.com.tamalesylechona.api.dto;

import com.tamalesylechona.api.model.Usuario;

public record LoginResponse(
        Integer idUsuario, String nombre, String apellido, String correo, String tipoUsuario) {

    public static LoginResponse desde(Usuario u) {
        return new LoginResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTipoUsuario());
    }
}
