package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.ZonaTrabajo;

public interface ZonaTrabajoRepository extends JpaRepository<ZonaTrabajo, Long> {

    Optional<ZonaTrabajo> findByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

}
