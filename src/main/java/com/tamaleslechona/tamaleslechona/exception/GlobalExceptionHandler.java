package com.tamaleslechona.tamaleslechona.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Un solo lugar para traducir excepciones de negocio a códigos HTTP.
// Así cada Controller nuevo (Usuario, Proveedor, Pedido, ...) no repite este código.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> manejarCredenciales(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    // Se dispara, por ejemplo, cuando el JSON del body viene mal formado,
    // o cuando una validación lanzada DENTRO de un setter (como la del password)
    // ocurre durante la deserialización, antes de llegar al Controller.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        Throwable causa = ex.getMostSpecificCause();
        String mensaje =
                causa instanceof IllegalArgumentException
                        ? causa.getMessage()
                        : "El cuerpo de la petición es inválido o no tiene el formato esperado.";
        return ResponseEntity.badRequest().body(new ErrorResponse(mensaje));
    }

    public record ErrorResponse(String mensaje) {}
}
