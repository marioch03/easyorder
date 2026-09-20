package com.easyorder.api.backend.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.easyorder.api.backend.dto.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
		log.error("Error no controlado: ", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Error interno del servidor: " + ex.getMessage()));
	}

	@ExceptionHandler(AsyncRequestNotUsableException.class)
	public void handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex) {
		log.debug("Cliente SSE desconectado: {}", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("Error de validación en la petición", errors));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("Parámetro inválido: " + ex.getName()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(SesionInvalidaException.class)
	public ResponseEntity<ErrorResponse> handleSesionInvalida(SesionInvalidaException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(NoEncontradoException.class)
	public ResponseEntity<ErrorResponse> handleEstadoNoEncontrado(NoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(RecursoExistenteException.class)
	public ResponseEntity<ErrorResponse> handleRecursoExistente(RecursoExistenteException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("Credenciales incorrectas"));
	}

	@ExceptionHandler(TokenInvalidoException.class)
	public ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(ProductoNoDisponibleException.class)
	public ResponseEntity<ErrorResponse> handleProductoNoDisponible(ProductoNoDisponibleException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(ex.getMessage()));
	}
}