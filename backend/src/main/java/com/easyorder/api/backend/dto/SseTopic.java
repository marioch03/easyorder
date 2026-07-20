package com.easyorder.api.backend.dto;

public enum SseTopic {
  MESAS("mesas"),
  PEDIDOS("pedidos"),
  KDS("kds");

  private final String value;

  SseTopic(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
