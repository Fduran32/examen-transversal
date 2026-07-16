package com.duoc.carrito.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<?> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<?> handleStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CarritoVacioException.class)
    public ResponseEntity<?> handleCarritoVacio(CarritoVacioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // Se lanza cuando NO se puede conectar al host (DNS no resuelve, connection refused, timeout, etc.)
    // Antes esto caía en un 500 genérico sin explicación.
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<?> handleWebClientRequest(WebClientRequestException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "No se pudo conectar con un servicio dependiente: " + ex.getMessage()));
    }

    // Se lanza cuando el otro microservicio SÍ responde, pero con un código de error (4xx/5xx)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleWebClientResponse(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", "Error desde servicio dependiente: " + ex.getMessage()));
    }

    // Red de seguridad final: cualquier otra excepción no prevista
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno inesperado: " + ex.getMessage()));
    }
}
