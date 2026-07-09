package com.mario.tfg.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.SesionEstado;

public interface SesionEstadoRepository extends JpaRepository<SesionEstado, Long> {
    Optional<SesionEstado> findByNombre(String nombre);
}
