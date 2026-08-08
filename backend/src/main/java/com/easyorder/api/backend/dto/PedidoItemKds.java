package com.easyorder.api.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoItemKds(
        Long id,
        String nombre,
        Long idProductoTipo,
        Integer cantidad,
        String nota,
        Integer mesa,
        boolean listoParaServir,
        LocalDateTime createdAt,
        List<ModificadorKdsDTO> modificadores) {
}