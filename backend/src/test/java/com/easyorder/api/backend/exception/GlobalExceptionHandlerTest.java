package com.easyorder.api.backend.exception;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test/endpoint");
    }

    @Test
    @DisplayName("SEC-03: Excepción no controlada no debe filtrar información sensible de BD ni SQL")
    void handleAllExceptions_excepcionConMensajeSensible_retornaMensajeOpacoYSeguro() {
        // Simular excepción con información técnica sensible de PostgreSQL/SQL
        Exception exSensible = new RuntimeException("ERROR: relation 'usuario' does not exist; SQL [SELECT u.password FROM usuario u WHERE u.id=1]");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleAllExceptions(exSensible, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("Ha ocurrido un error interno en el servidor. Por favor, inténtelo de nuevo más tarde o contacte con soporte si el problema persiste.");
        assertThat(body.getInstance().toString()).isEqualTo("/api/v1/test/endpoint");
        assertThat(body.getProperties().get("message")).isEqualTo(body.getDetail());
        assertThat(body.getProperties().get("timestamp")).isNotNull();

        // Verificar rigurosamente que NO contiene detalles de SQL ni del mensaje interno
        assertThat(body.getDetail()).doesNotContain("relation 'usuario'");
        assertThat(body.getDetail()).doesNotContain("SELECT");
    }

    @Test
    @DisplayName("SEC-04: NoEncontradoException debe retornar ProblemDetail con status 404")
    void handleNoEncontradoException_retornaProblemDetail404() {
        NoEncontradoException ex = new NoEncontradoException("Mesa no encontrada. Id: 99");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleEstadoNoEncontrado(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getTitle()).isEqualTo("Not Found");
        assertThat(body.getDetail()).isEqualTo("Mesa no encontrada. Id: 99");
        assertThat(body.getProperties().get("message")).isEqualTo("Mesa no encontrada. Id: 99");
    }

    @Test
    @DisplayName("SEC-04: MethodArgumentNotValidException debe retornar ProblemDetail 400 con lista de errores")
    void handleValidationExceptions_retornaProblemDetail400ConListaErrores() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "crearProductoDTO");
        bindingResult.addError(new FieldError("crearProductoDTO", "nombre", "El nombre es obligatorio"));
        bindingResult.addError(new FieldError("crearProductoDTO", "precio", "El precio no puede ser negativo"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleValidationExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getTitle()).isEqualTo("Bad Request");
        assertThat(body.getDetail()).isEqualTo("Error de validación en la petición");

        @SuppressWarnings("unchecked")
        java.util.List<String> errors = (java.util.List<String>) body.getProperties().get("errors");
        assertThat(errors).containsExactlyInAnyOrder(
                "nombre: El nombre es obligatorio",
                "precio: El precio no puede ser negativo"
        );
    }

    @Test
    @DisplayName("SEC-04: RecursoExistenteException debe retornar ProblemDetail 409 Conflict")
    void handleRecursoExistente_retornaProblemDetail409() {
        RecursoExistenteException ex = new RecursoExistenteException("La mesa ya existe");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleRecursoExistente(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(409);
        assertThat(body.getDetail()).isEqualTo("La mesa ya existe");
    }
}
