package com.easyorder.api.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String message,
    List<String> errors,
    LocalDateTime timestamp) {
  public ErrorResponse(String message) {
    this(message, List.of(), LocalDateTime.now());
  }

  public ErrorResponse(String message, List<String> errors) {
    this(message, errors, LocalDateTime.now());
  }
}
