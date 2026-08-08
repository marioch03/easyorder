package com.easyorder.api.backend.dto;

import java.util.List;

public record GrupoModificadorDTO(
    Long id,
    String nombre,
    Integer seleccionMinima,
    Integer seleccionMaxima,
    List<ModificadorDTO> modificadores) {
}