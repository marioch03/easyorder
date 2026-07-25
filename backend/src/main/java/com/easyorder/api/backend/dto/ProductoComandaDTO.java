package com.easyorder.api.backend.dto;

import java.math.BigDecimal;

public record ProductoComandaDTO(
    Long id,
    String nombre,
    BigDecimal precio,
    Long idTipoProducto,
    Boolean disponible) {
}
