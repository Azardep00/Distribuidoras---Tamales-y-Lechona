package main.java.com.tamalesylechona.api.exception;

// Excepción genérica para cuando un recurso (Producto, Usuario, etc.) no existe.
// Antes cada Service tenía su propia excepción anidada; la centralizamos aquí
// para no repetir el mismo @ExceptionHandler en cada Controller nuevo.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
