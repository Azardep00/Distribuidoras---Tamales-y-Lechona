package com.tamaleslechona.tamaleslechona.exception;

// Se lanza cuando la operación choca con el estado actual de los datos
// (por ejemplo, borrar un proveedor que ya tiene movimientos de inventario).
// El GlobalExceptionHandler la traduce a HTTP 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}
