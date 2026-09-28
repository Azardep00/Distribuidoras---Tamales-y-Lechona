package com.tamaleslechona.tamaleslechona.exception;

// Se lanza cuando una IP o un correo acumula demasiados intentos fallidos de
// login. GlobalExceptionHandler la traduce a 429 Too Many Requests.
public class DemasiadosIntentosException extends RuntimeException {

    public DemasiadosIntentosException(String mensaje) {
        super(mensaje);
    }
}