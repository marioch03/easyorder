package com.easyorder.api.backend.dto;

public record MesaDTO(
                Long id,
                int numero,
                String estado,
                String zona,
                SesionDTO sesionActiva) {
}