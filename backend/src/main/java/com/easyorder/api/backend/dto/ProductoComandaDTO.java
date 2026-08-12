package com.easyorder.api.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoComandaDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        Long idTipoProducto,
        Boolean disponible,
        List<GrupoModificadorDTO> gruposModificadores) {
}
