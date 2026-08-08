package com.easyorder.api.backend.dto;

import java.math.BigDecimal;

public record PedidoItem_ModificadorDTO(
        Long id,
        String nombre,
        BigDecimal precioAplicado) {
}
