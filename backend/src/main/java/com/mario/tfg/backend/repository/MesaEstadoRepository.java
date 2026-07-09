package com.mario.tfg.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.MesaEstado;

public interface MesaEstadoRepository extends JpaRepository<MesaEstado, Long> {
    Optional<MesaEstado> findByNombre(String nombre);
}
