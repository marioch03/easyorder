package com.easyorder.api.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearProductoDTO(
        @NotBlank(message = "El nombre del producto no puede estar vacío")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        @Digits(integer = 8, fraction = 2, message = "El precio debe tener como máximo 2 decimales")
        BigDecimal precio,

        @NotNull(message = "El tipo de producto es obligatorio")
        Long tipoId,

        Boolean disponible,

        @Size(max = 200, message = "La ruta de la imagen no puede superar los 200 caracteres")
        String imagen
) {
}
