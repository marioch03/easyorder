package com.easyorder.api.backend.exception;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones estandarizado con el RFC 7807 (ProblemDetail).
 * Centraliza la captura de errores, previene la fuga de información sensible
 * en excepciones no controladas y proporciona respuestas HTTP homogéneas.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MENSAJE_ERROR_INTERNO =
            "Ha ocurrido un error interno en el servidor. Por favor, inténtelo de nuevo más tarde o contacte con soporte si el problema persiste.";

    /**
     * [SEC-03] Manejador de excepciones genéricas no controladas.
     * Registra el stack trace completo internamente pero emite un mensaje opaco y seguro al cliente,
     * evitando la exposición de consultas SQL, tablas o trazas internas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado procesando solicitud en {}: ",
                request != null ? request.getRequestURI() : "N/A", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_ERROR_INTERNO, request);
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex) {
        log.debug("Cliente SSE desconectado: {}", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        ProblemDetail problem = createProblemDetail(
                HttpStatus.BAD_REQUEST, "Error de validación en la petición", request);
        problem.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Parámetro inválido: " + ex.getName(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(SesionInvalidaException.class)
    public ResponseEntity<ProblemDetail> handleSesionInvalida(
            SesionInvalidaException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleEstadoNoEncontrado(
            NoEncontradoException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado: " + ex.getResourcePath(), request);
    }

    @ExceptionHandler(RecursoExistenteException.class)
    public ResponseEntity<ProblemDetail> handleRecursoExistente(
            RecursoExistenteException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", request);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ProblemDetail> handleTokenInvalido(
            TokenInvalidoException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ProductoNoDisponibleException.class)
    public ResponseEntity<ProblemDetail> handleProductoNoDisponible(
            ProductoNoDisponibleException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({ ObjectOptimisticLockingFailureException.class, OptimisticLockException.class })
    public ResponseEntity<ProblemDetail> handleOptimisticLocking(
            Exception ex, HttpServletRequest request) {
        log.warn("Conflicto de concurrencia optimista detectado: {}", ex.getMessage());
        String mensaje = "El recurso ha sido modificado concurrentemente por otro usuario o proceso. Por favor, refresque la información y reintente la acción.";
        return buildResponse(HttpStatus.CONFLICT, mensaje, request);
    }

    // --- Métodos auxiliares de construcción RFC 7807 ---

    private ResponseEntity<ProblemDetail> buildResponse(
            HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problem = createProblemDetail(status, detail, request);
        return ResponseEntity.status(status).body(problem);
    }

    private ProblemDetail createProblemDetail(
            HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        if (request != null && request.getRequestURI() != null) {
            try {
                problem.setInstance(URI.create(request.getRequestURI()));
            } catch (Exception ignored) {
                // Si la URI no cumple formato estándar, continúa sin instance
            }
        }
        problem.setProperty("timestamp", Instant.now().toString());
        // Propiedad "message" para garantizar 100% de retrocompatibilidad con frontend y tests
        problem.setProperty("message", detail);
        return problem;
    }
}