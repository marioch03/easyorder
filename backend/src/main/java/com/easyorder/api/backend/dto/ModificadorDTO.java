package com.easyorder.api.backend.dto;

import java.math.BigDecimal;

public record ModificadorDTO(
        Long id,
        String nombre,
        BigDecimal precioExtra) {
}
