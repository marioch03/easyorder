package com.easyorder.api.backend.dto;

import java.time.Instant;
import java.util.List;

public record PedidoItemKds(
		Long id,
		String nombre,
		Long idProductoTipo,
		Integer cantidad,
		String nota,
		Integer mesa,
		boolean listoParaServir,
		Instant createdAt,
		List<ModificadorKdsDTO> modificadores) {
}