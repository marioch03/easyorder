package com.mario.tfg.backend.dto;

public record ProductoDTO(
        long id,
        String nombre,
        String descripcion,
        double precio,
        boolean disponible,
        String imagen,
        long idTipo) {
}