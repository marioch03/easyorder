package com.easyorder.api.backend.exception;

public class RecursoExistenteException extends RuntimeException {
    public RecursoExistenteException(String mensaje) {
        super(mensaje);
    }

}
