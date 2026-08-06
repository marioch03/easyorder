package com.easyorder.api.backend.dto;

public record LoginRequest(String nombre, String clave, String tenantSlug) {
}
