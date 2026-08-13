package com.easyorder.api.backend.dto;

public record EditarProductoDTO(
		Long id,
		String nombre,
		String descripcion,
		Double precio,
		Long tipoId,
		Boolean disponible) {
}
