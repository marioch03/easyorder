package com.easyorder.api.backend.dto;

import java.time.LocalDateTime;

public record PedidoItemKds(
    Long id,
    String nombre,
    Long idProductoTipo,
    Integer cantidad,
    String nota,
    Integer mesa,
    boolean listoParaServir,
    LocalDateTime createdAt) {
}