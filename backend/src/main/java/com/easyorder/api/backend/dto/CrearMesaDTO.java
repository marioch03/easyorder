package com.easyorder.api.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CrearMesaDTO(
                @NotNull(message = "El número de mesa es obligatorio") @Min(value = 1, message = "El número de mesa debe ser mayor que 0") Integer numero,

                @NotNull(message = "La zona es obligatoria") Long idZona

) {
}