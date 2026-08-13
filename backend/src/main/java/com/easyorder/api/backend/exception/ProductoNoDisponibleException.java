package com.easyorder.api.backend.exception;

public class ProductoNoDisponibleException extends RuntimeException {
  public ProductoNoDisponibleException(String mensaje) {
    super(mensaje);
  }

}
