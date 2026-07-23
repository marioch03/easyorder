package com.easyorder.api.backend.dto;

public enum PedidoEstadoEnum {
  PENDIENTE("PENDIENTE"),
  PARCIAL("PARCIAL"),
  LISTO("LISTO"),
  SERVIDO("SERVIDO");

  private final String value;

  PedidoEstadoEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
