package com.mario.tfg.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record CuentaDTO(
                List<PedidoItemDTO> items,

                @NotNull(message = "El total de la cuenta no puede ser nulo") BigDecimal total) {
        public CuentaDTO {
                if (items == null) {
                        items = List.of();
                }
        }
}