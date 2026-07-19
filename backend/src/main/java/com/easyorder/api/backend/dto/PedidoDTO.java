package com.easyorder.api.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoDTO(
		Long idPedido,

		@NotNull(message = "El id de sesión es obligatorio") Long idSesion,

		@Min(value = 1, message = "El número de mesa debe ser igual o mayor a 1") int numeroMesa,

		String nombreEstado,

		LocalDateTime createdAt,

		@NotEmpty(message = "El pedido debe contener al menos un artículo") @Valid List<PedidoItemDTO> items) {
	public PedidoDTO {
		if (items == null) {
			items = List.of();
		}
	}
}