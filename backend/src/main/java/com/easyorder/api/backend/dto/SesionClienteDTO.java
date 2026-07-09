package com.easyorder.api.backend.dto;

import java.io.Serializable;

public record SesionClienteDTO(
                Long sesionId,
                String sessionCode,
                Long mesaId,
                int numeroMesa,
                String estadoMesa) implements Serializable {
}
