package com.easyorder.api.backend.dto;

public record PedidoItemKds(
    Long id,
    String nombre,
    Integer cantidad,
    String nota,
    Integer mesa,
    boolean listoParaServir) {
}