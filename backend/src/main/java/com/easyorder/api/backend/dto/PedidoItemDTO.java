package com.easyorder.api.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoItemDTO(
                @NotNull(message = "El ID del producto es obligatorio") Long idProducto,

                String nombreProducto,

                @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad mínima debe ser 1") Integer cantidad,

                @NotNull(message = "El precio unitario es obligatorio") BigDecimal precioUnitario,

                @Size(max = 255, message = "La nota es demasiado larga (máximo 255 caracteres)") String nota) {
}