package com.easyorder.api.backend.dto;

import java.util.Arrays;

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

  public static SseTopic fromValue(String value) {

    return Arrays.stream(values())

        .filter(topic -> topic.value.equalsIgnoreCase(value))

        .findFirst()

        .orElseThrow(() ->

        new IllegalArgumentException(

            "Topic SSE no válido: " + value));

  }
}