package com.easyorder.api.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAllExceptions(Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(" Error: " + ex.getMessage());
        }

        @ExceptionHandler(AsyncRequestNotUsableException.class)
        public void handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex) {
                log.debug("Cliente SSE desconectado: {}", ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
                String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error de validación: " + errorMsg);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Parámetro inválido: " + ex.getName());
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(SesionInvalidaException.class)
        public ResponseEntity<String> handleSesionInvalida(
                        SesionInvalidaException ex) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(NoEncontradoException.class)
        public ResponseEntity<String> handleEstadoNoEncontrado(
                        NoEncontradoException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(RecursoExistenteException.class)
        public ResponseEntity<String> handleRecursoExistente(
                        RecursoExistenteException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Credenciales incorrectas");
        }

        @ExceptionHandler(TokenInvalidoException.class)
        public ResponseEntity<String> handleTokenInvalido(TokenInvalidoException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ex.getMessage());
        }
}