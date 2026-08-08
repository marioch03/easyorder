package com.easyorder.api.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CrearPedidoDTO(
    @NotEmpty(message = "El pedido debe contener al menos un artículo") @Valid List<CrearPedidoItemDTO> items,
    BigDecimal total) {
}