package com.tamaleslechona.tamaleslechona.exception;

// Se lanza cuando el correo no existe, la contraseña no coincide,
// o el usuario está inactivo al intentar iniciar sesión.
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
